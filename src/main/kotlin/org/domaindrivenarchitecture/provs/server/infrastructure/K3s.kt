package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.core.repeatTaskUntilSuccess
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.*

private const val k3sConfigFile = "/etc/rancher/k3s/config.yaml"
private const val k3sCalicoFile = "/var/lib/rancher/k3s/server/manifests/calico.yaml"
private const val k3sAppleFile = "/var/lib/rancher/k3s/server/manifests/apple.yaml"
private const val certManagerDeployment = "/etc/rancher/k3s/certmanager.yaml"
private const val certManagerIssuer = "/etc/rancher/k3s/issuer.yaml"
private const val k3sInstallFile = "/usr/local/bin/k3s-install.sh"
private const val k3sResourcePath = "org/domaindrivenarchitecture/provs/infrastructure/k3s/"

enum class CertManagerEndPoint {
    STAGING, PROD
}


fun Prov.testConfigExists(): Boolean {
    return fileExists(k3sConfigFile)
}

fun Prov.deprovisionK3sInfra() = task {
    //deleteFile(k3sCalicoFile, sudo = true)
    deleteFile(k3sInstallFile, sudo = true)
    cmd("k3s-uninstall.sh")
}

/**
 * Installs a k3s server.
 * If docker is true, then docker will be installed (may conflict if docker is already existing) and k3s will be installed with docker option.
 * If tlsHost is specified, then tls (if configured) also applies to the specified host.
 */
fun Prov.provisionK3sInfra(tlsName: String, nodeIpv4: String, loopbackIpv4: String, loopbackIpv6: String,
                           nodeIpv6: String? = null, docker: Boolean = false, installApple: Boolean = false,
                           tlsHost: String? = null) = task {
    val isDualStack = nodeIpv6?.isNotEmpty() ?: false
    if (testConfigExists()) {
        deprovisionK3sInfra()
    }
    if (!testConfigExists()) {
        createDirs("/etc/rancher/k3s/", sudo = true)
        var k3sConfigFileName = "config.yaml.template"
        var k3sConfigMap: Map<String, String> = mapOf("loopback_ipv4" to loopbackIpv4, "loopback_ipv6" to loopbackIpv6,
            "node_ipv4" to nodeIpv4, "tls_name" to tlsName)
        if (isDualStack) {
            k3sConfigFileName += ".dual"
            k3sConfigMap = k3sConfigMap.plus("node_ipv6" to nodeIpv6!!)
            /*
            createFileFromResource(
                k3sCalicoFile,
                "calico.yaml",
                k3sResourcePath,
                "644",
                sudo = true
            )
             */
        } else {
            k3sConfigFileName += ".ipv4"
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
            k3sInstallFile,
            "k3s-install.sh",
            k3sResourcePath,
            "755",
            sudo = true
        )
        // TODO: does not work yet cmd("k3s-install.sh")
        cmd("sh /root/k3s-install.sh")
        createFileFromResource(
            k3sAppleFile,
            "apple.yaml",
            k3sResourcePath,
            "644",
            sudo = true
        )
        /*

        org/domaindrivenarchitecture/provs/infrastructure/k3s/config.yaml.template.template

        val tlsSanOption = tlsHost?.let { "--tls-san ${it}" } ?: ""

        val k3sAllOptions = if (tlsHost == null && options == null)
            ""
        else
            "INSTALL_K3S_EXEC=\"$tlsSanOption ${options ?: ""}\""

        aptInstall("curl")
        if (!chk("k3s -version")) {
            if (docker) {
                // might not work if docker already installed
                sh(
                    """
                curl https://releases.rancher.com/install-docker/19.03.sh | sh
                curl -sfL https://get.k3s.io | $k3sAllOptions sh -s - --docker
            """.trimIndent()
                )
            } else {
                cmd("curl -sfL https://get.k3s.io | $k3sAllOptions sh -")
            }
        }
         */
    } else {
        ProvResult(true)
    }
}


fun Prov.provisionK3sCertManager(endpoint: CertManagerEndPoint) = task {
    createFileFromResource(
        certManagerDeployment,
        "cert-manager.yaml",
        k3sResourcePath,
        "644",
        sudo = true
    )
    createFileFromResourceTemplate(
        certManagerIssuer,
        "le-issuer.template.yaml",
        k3sResourcePath,
        mapOf("endpoint" to endpoint.name.lowercase()),
        "644",
        sudo = true
    )
    cmd("kubectl apply -f $certManagerDeployment", sudo = true)

    repeatTaskUntilSuccess(10, 10) {
        cmd("kubectl apply -f $certManagerIssuer", sudo = true)
    }
}

/*
@Suppress("unused")
fun Prov.uninstallK3sServer() = task {
    cmd("sudo /usr/local/bin/k3s-uninstall.sh")
}


fun Prov.applyK3sConfig(configAsYaml: String) = task {
    cmd(echoCommandForText(configAsYaml) + " | sudo k3s kubectl apply -f -")
}
*/