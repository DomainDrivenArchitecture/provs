package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.core.repeatTaskUntilSuccess
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.*
import org.domaindrivenarchitecture.provs.server.domain.CertmanagerEndpoint
import org.domaindrivenarchitecture.provs.server.domain.k3s.*
import java.io.File

// -----------------------------------  versions  --------------------------------

// when updating this version, it is recommended to update also file k3s-install.sh as well as traefik.yaml in this repo
// (both files in: src/main/resources/org/domaindrivenarchitecture/provs/server/infrastructure/k3s/)
const val K3S_VERSION = "v1.29.1+k3s2"

// -----------------------------------  directories  --------------------------------
const val k3sManualManifestsDir = "/etc/rancher/k3s/manifests/"

private const val k3sAutomatedManifestsDir = "/var/lib/rancher/k3s/server/manifests/"
private const val k8sCredentialsDir = "/etc/kubernetes/"

private const val k3sResourceDir = "org/domaindrivenarchitecture/provs/server/infrastructure/k3s/"

// -----------------------------------  files  --------------------------------

private val k3sInstallScript = File("/usr/local/bin/k3s-install.sh")
private val k3sConfigFile = File("/etc/rancher/k3s/config.yaml")
private val k3sKubeConfig = File("/etc/rancher/k3s/k3s.yaml")

private val k3sTraefikWorkaround = File(k3sManualManifestsDir, "traefik.yaml")
private val k3sMiddleWareHttpsRedirect = File(k3sManualManifestsDir, "middleware.yaml")
private val certManagerDeployment = File(k3sManualManifestsDir, "cert-manager.yaml")

private val certManagerIssuer = File(k3sManualManifestsDir, "le-issuer.yaml")
private val k3sEchoWithTls = File(k3sManualManifestsDir, "echo-tls.yaml")
private val k3sEchoNoTls = File(k3sManualManifestsDir, "echo-no-tls.yaml")
private val selfSignedCertificate = File(k3sManualManifestsDir, "selfsigned-certificate.yaml")

private val localPathProvisionerConfig = File(k3sManualManifestsDir, "local-path-provisioner-config.yaml")

// -----------------------------------  public functions  --------------------------------

fun Prov.testConfigExists(): Boolean {
    return checkFile(k3sConfigFile.path)
}


fun Prov.deprovisionK3sInfra() = task {
    deleteFile(k3sInstallScript.path, sudo = true)
    deleteFile(certManagerDeployment.path, sudo = true)
    deleteFile(certManagerIssuer.path, sudo = true)
    deleteFile(k3sKubeConfig.path, sudo = true)

    val k3sUninstallScript = "k3s-uninstall.sh"
    if (chk("which $k3sUninstallScript")) {
        cmd(k3sUninstallScript)
    }
}


fun Prov.installK3s(k3sConfig: K3sConfig): ProvResult {
    return taskWithResult {
        if (testConfigExists()) {
            return@taskWithResult ProvResult(true, out = "K3s config is already in place, so skip (re)provisioning.")
        }

        createDirs(k8sCredentialsDir, sudo = true)
        createDirs(k3sAutomatedManifestsDir, sudo = true)
        createDirs(k3sManualManifestsDir, sudo = true)
        createDirs("/var/pvc1", sudo = true)
        createDirs("/var/pvc2", sudo = true)

        var k3sConfigMap: Map<String, String> = mapOf(
            "loopback_ipv4" to k3sConfig.loopback.ipv4,
            "node_ipv4" to k3sConfig.node.ipv4,
            "tls_name" to k3sConfig.fqdn
        )
        var k3sConfigResourceFileName = "config"
        var metallbConfigResourceFileName = "metallb-config"
        if (k3sConfig.isDualStack()) {
            k3sConfigResourceFileName += ".dual.template.yaml"
            metallbConfigResourceFileName += ".dual.template.yaml"
            require(k3sConfig.node.ipv6 != null && k3sConfig.loopback.ipv6 != null)
            k3sConfigMap = k3sConfigMap
                .plus("node_ipv6" to k3sConfig.node.ipv6)
                .plus("loopback_ipv6" to k3sConfig.loopback.ipv6)
        } else {
            k3sConfigResourceFileName += ".ipv4.template.yaml"
            metallbConfigResourceFileName += ".ipv4.template.yaml"
        }

        createK3sFileFromResourceTemplate(
            k3sConfigFile,
            k3sConfigMap,
            alternativeResourceTemplate = File(k3sConfigResourceFileName)
        )
        createK3sFileFromResource(k3sInstallScript, posixFilePermission = "755")
        cmd("INSTALL_K3S_VERSION=$K3S_VERSION k3s-install.sh")

        // metallb
        applyK3sFileFromResource(File(k3sManualManifestsDir, "metallb-0.15.2-native-manifest.yaml"))

        repeatTaskUntilSuccess(10, 10) {
            applyK3sFileFromResourceTemplate(
                File(k3sManualManifestsDir, "metallb-config.yaml"),
                k3sConfigMap,
                alternativeResourceName = File(metallbConfigResourceFileName)
            )
        }

        applyK3sFileFromResource(File(k3sManualManifestsDir, "metallb-l2advertisement.yaml"))

        // traefik
        if (k3sConfig.isDualStack()) {
            // see https://github.com/k3s-io/k3s/discussions/5003
            applyK3sFileFromResource(k3sTraefikWorkaround)
        } else {
            ProvResult(true)
        }

        repeatTaskUntilSuccess(6, 20) {
            applyK3sFileFromResource(k3sMiddleWareHttpsRedirect)
        }

        // other
        applyK3sFileFromResource(localPathProvisionerConfig)

        cmd("kubectl set env deployment -n kube-system local-path-provisioner DEPLOY_DATE=\"$(date)\"", sudo = true)

        cmd("ln -sf $k3sKubeConfig " + k8sCredentialsDir + "admin.conf", sudo = true)

        configureShellAliases()

        configureSsh()
    }
}

fun Prov.provisionK3sCertManager(certmanager: Certmanager) = task {

    applyK3sFileFromResource(certManagerDeployment)

    val issuerMap = mapOf(
        "endpoint" to certmanager.letsencryptEndpoint.endpointUri(),
        "name" to certmanager.letsencryptEndpoint.name.lowercase(),
        "email" to certmanager.email
    )
    createK3sFileFromResourceTemplate(certManagerIssuer, issuerMap)
    repeatTaskUntilSuccess(10, 10) {
        applyK3sFile(certManagerIssuer)
    }
}

fun Prov.provisionK3sEcho(fqdn: String, endpoint: CertmanagerEndpoint? = null, withTls: Boolean = false) = task {
    if (withTls) {
        val endpointName = endpoint?.name?.lowercase()

        val issuer = if (endpointName == null) {
            createK3sFileFromResourceTemplate(selfSignedCertificate, mapOf("host" to fqdn))
            "selfsigned-issuer"
        } else {
            endpointName
        }
        applyK3sFileFromResourceTemplate(k3sEchoWithTls, mapOf("fqdn" to fqdn, "issuer_name" to issuer))
    } else {
        applyK3sFileFromResource(k3sEchoNoTls)
    }
}

fun Prov.provisionK3sApplication(applicationFile: ApplicationFile) = task {
    val remoteFqFilename = k3sManualManifestsDir + applicationFile.fileName.name()
    createFile(
        remoteFqFilename,
        posixFilePermission = "644",
        sudo = true,
        text = applicationFile.fileContent
    )
    applyK3sFile(File(remoteFqFilename))
}


// ============================  functions to create k3s files and apply  =============================

fun Prov.applyK3sFile(file: File) = task {
    cmd("kubectl apply -f ${file.path}", sudo = true)
}

fun Prov.createK3sFileFromResource(
    file: File,
    posixFilePermission: FileMode? = "644"
) = task {
    createFileFromResource(
        file.path,
        file.name,
        k3sResourceDir,
        posixFilePermission,
        sudo = true
    )
}

fun Prov.applyK3sFileFromResource(file: File, posixFilePermission: String? = "644") = task {
    createK3sFileFromResource(file, posixFilePermission)
    applyK3sFile(file)
}

fun Prov.applyK3sFileFromResourceTemplate(
    file: File,
    values: Map<String, String>,
    posixFilePermission: String? = "644",
    alternativeResourceName: File? = null
) = task {
    createK3sFileFromResourceTemplate(file, values, posixFilePermission, alternativeResourceName)
    applyK3sFile(file)
}

fun Prov.createK3sFileFromResourceTemplate(
    file: File,
    values: Map<String, String>,
    posixFilePermission: String? = "644",
    alternativeResourceTemplate: File? = null
) = task {
    createFileFromResourceTemplate(
        file.path,
        alternativeResourceTemplate?.name ?: file.templateName(),
        k3sResourceDir,
        values,
        posixFilePermission,
        sudo = true
    )
}

private fun File.templateName(): String {
    return this.name.replace(".yaml", ".template.yaml")
}

internal fun Prov.configureShellAliases() = task {
    addTextToFile("\nalias k=\"sudo kubectl\"\n", File(".bash_aliases"))
}