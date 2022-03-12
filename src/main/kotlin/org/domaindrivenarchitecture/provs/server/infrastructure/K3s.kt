package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.core.repeatTaskUntilSuccess
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.*
import org.domaindrivenarchitecture.provs.server.domain.CertmanagerEndpoint
import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFileName
import org.domaindrivenarchitecture.provs.server.domain.k3s.Certmanager
import org.domaindrivenarchitecture.provs.server.domain.k3s.K3sConfig

private const val k3sResourcePath = "org/domaindrivenarchitecture/provs/server/infrastructure/k3s/"
private const val k3sManualManifestsDir = "/etc/rancher/k3s/manifests/"
private const val k8sCredentialsPath = "/etc/kubernetes/"
private const val k3sAutomatedManifestsDir = "/var/lib/rancher/k3s/server/manifests/"
private const val k3sConfigFile = "/etc/rancher/k3s/config.yaml"
private const val k3sTraefikWorkaround = k3sManualManifestsDir + "traefik.yaml"
private const val certManagerDeployment = k3sManualManifestsDir + "certmanager.yaml"
private const val certManagerIssuer = k3sManualManifestsDir + "issuer.yaml"
private const val selfsignedCertificate = k3sManualManifestsDir + "selfsigned-certificate.yaml"
private const val k3sApple = k3sManualManifestsDir + "apple.yaml"
private const val k3sEcho = k3sManualManifestsDir + "echo.yaml"
private const val k3sInstall = "/usr/local/bin/k3s-install.sh"


fun Prov.testConfigExists(): Boolean {
    return fileExists(k3sConfigFile)
}

fun Prov.provisionK3sInfra(k3sConfig: K3sConfig) = task {
    if (!testConfigExists()) {
        installK3s(k3sConfig)
    } else {
        ProvResult(true)
    }
}

fun Prov.deprovisionK3sInfra() = task {
    deleteFile(k3sInstall, sudo = true)
    deleteFile(k3sApple, sudo = true)
    deleteFile(certManagerDeployment, sudo = true)
    deleteFile(certManagerIssuer, sudo = true)
    cmd("k3s-uninstall.sh")
}


fun Prov.installK3s(k3sConfig: K3sConfig) = task {
    createDirs(k8sCredentialsPath, sudo = true)
    createDirs(k3sAutomatedManifestsDir, sudo = true)
    createDirs(k3sManualManifestsDir, sudo = true)
    var k3sConfigFileName = "config"
    var metallbConfigFileName = "metallb-config"
    var k3sConfigMap: Map<String, String> = mapOf(
        "loopback_ipv4" to k3sConfig.loopback.ipv4,
        "node_ipv4" to k3sConfig.node.ipv4, "tls_name" to k3sConfig.fqdn
    )
    if (k3sConfig.isDualStack()) {
        k3sConfigFileName += ".dual.template.yaml"
        metallbConfigFileName += ".dual.template.yaml"
        k3sConfigMap = k3sConfigMap.plus("node_ipv6" to k3sConfig.node.ipv6!!)
            .plus("loopback_ipv6" to k3sConfig.loopback.ipv6!!)
    } else {
        k3sConfigFileName += ".ipv4.template.yaml"
        metallbConfigFileName += ".ipv4.template.yaml"
    }
    createFileFromResourceTemplate(
        k3sConfigFile,
        k3sConfigFileName,
        k3sResourcePath,
        k3sConfigMap,
        "644",
        sudo = true
    )
    createFileFromResource(
        k3sInstall,
        "k3s-install.sh",
        k3sResourcePath,
        "755",
        sudo = true
    )
    cmd("INSTALL_K3S_CHANNEL=latest k3s-install.sh")

    // metallb
    createFileFromResource(
        k3sManualManifestsDir + "metallb-namespace.yaml",
        "metallb-namespace.yaml",
        k3sResourcePath,
        sudo = true
    )
    createFileFromResource(
        k3sManualManifestsDir + "metallb-manifest.yaml",
        "metallb-0.10.2-manifest.yaml",
        k3sResourcePath,
        sudo = true
    )
    createFileFromResourceTemplate(
        k3sManualManifestsDir + "metallb-config.yaml",
        metallbConfigFileName,
        k3sResourcePath,
        k3sConfigMap,
        "644",
        sudo = true
    )
    cmd("kubectl apply -f ${k3sManualManifestsDir}metallb-namespace.yaml", sudo = true)
    cmd("kubectl apply -f ${k3sManualManifestsDir}metallb-manifest.yaml", sudo = true)
    cmd("kubectl apply -f ${k3sManualManifestsDir}metallb-config.yaml", sudo = true)

    // traefic
    if (k3sConfig.isDualStack()) {
        // see https://github.com/k3s-io/k3s/discussions/5003
        createFileFromResource(
            k3sTraefikWorkaround,
            "traefik.yaml",
            k3sResourcePath,
            "644",
            sudo = true
        )
        cmd("kubectl apply -f $k3sTraefikWorkaround", sudo = true)
    } else {
        ProvResult(true)
    }
    cmd("ln -s /etc/rancher/k3s/k3s.yaml " + k8sCredentialsPath + "admin.conf", sudo = true)
}

fun Prov.provisionK3sCertManager(certmanager: Certmanager) = task {
    createFileFromResource(
        certManagerDeployment,
        "cert-manager.yaml",
        k3sResourcePath,
        "644",
        sudo = true
    )
    cmd("kubectl apply -f $certManagerDeployment", sudo = true)
    createFileFromResourceTemplate(
        certManagerIssuer,
        "le-issuer.template.yaml",
        k3sResourcePath,
        mapOf(
            "endpoint" to certmanager.letsencryptEndpoint.endpointUri(),
            "name" to certmanager.letsencryptEndpoint.name.lowercase(),
            "email" to certmanager.email
        ),
        "644",
        sudo = true
    )
    repeatTaskUntilSuccess(10, 10) {
        cmd("kubectl apply -f $certManagerIssuer", sudo = true)
    }
}

fun Prov.provisionK3sEcho(fqdn: String, endpoint: CertmanagerEndpoint? = null) = task {
    val endpointName = endpoint?.name?.lowercase()

    val issuer = if (endpointName != null)
        endpointName
    else {
        createFileFromResourceTemplate(
            selfsignedCertificate,
            "selfsigned-certificate.template.yaml",
            k3sResourcePath,
            mapOf("host" to fqdn),
            "644",
            sudo = true
        )
        "selfsigned-issuer"
    }

    createFileFromResourceTemplate(
        k3sEcho,
        "echo.template.yaml",
        k3sResourcePath,
        mapOf("fqdn" to fqdn, "issuer_name" to issuer),
        "644",
        sudo = true
    )
    cmd("kubectl apply -f $k3sEcho", sudo = true)
}

fun Prov.provisionK3sApplication(applicationFileName: ApplicationFileName) = task {
    copyFileFromLocal(
        fullyQualifiedLocalFilename = applicationFileName.fullqualified(),
        fullyQualifiedFilename = k3sManualManifestsDir + "application.yaml",
        posixFilePermission = "644",
        sudo = true
    )
    cmd("kubectl apply -f ${k3sManualManifestsDir}application.yaml", sudo = true)
}
