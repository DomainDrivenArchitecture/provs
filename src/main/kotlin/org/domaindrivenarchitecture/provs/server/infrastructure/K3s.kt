package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDirs
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFileFromResourceTemplate
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.fileExists

// TODO: jem - 2022.01.24 - these are global vars without scope / ns !
val k3sConfigFile = "/etc/rancher/k3s/config.yaml"
val k3sResourcePath = "org/domaindrivenarchitecture/provs/infrastructure/k3s/"

fun Prov.testConfigExists(): Boolean {
    return fileExists(k3sConfigFile)
}

/**
 * Installs a k3s server.
 * If docker is true, then docker will be installed (may conflict if docker is already existing) and k3s will be installed with docker option.
 * If tlsHost is specified, then tls (if configured) also applies to the specified host.
 */
fun Prov.provisionK3sInfra(docker: Boolean = false, tlsHost: String? = null, options: String? = null) = task {
    if (!testConfigExists()) {
        createDirs("/etc/rancher/k3s/", sudo = true)
        createFileFromResourceTemplate(
            k3sConfigFile,
            "config.yaml.template",
            k3sResourcePath,
            mapOf("loopback_ipv4" to "192.168.5.1", "loopback_ipv6" to "fc00::5:1",
            "node_ipv4" to "159.69.176.151", "node_ipv6" to "2a01:4f8:c010:2f72::1"),
            "644",
            sudo = true
        )
        // TODO: verify the download !
        //cmd("curl -sfL https://get.k3s.io | sh -")

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

/*
@Suppress("unused")
fun Prov.uninstallK3sServer() = task {
    cmd("sudo /usr/local/bin/k3s-uninstall.sh")
}


fun Prov.applyK3sConfig(configAsYaml: String) = task {
    cmd(echoCommandForText(configAsYaml) + " | sudo k3s kubectl apply -f -")
}
*/