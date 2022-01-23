package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.core.echoCommandForText
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall

/**
 * Installs a k3s server.
 * If docker is true, then docker will be installed (may conflict if docker is already existing) and k3s will be installed with docker option.
 * If tlsHost is specified, then tls (if configured) also applies to the specified host.
 */
fun Prov.provisionK3sInfra(docker: Boolean = false, tlsHost: String? = null, options: String? = null) = task {
    val tlsSanOption = tlsHost?.let { "--tls-san ${it}" } ?: ""

    val k3sAllOptions = if (tlsHost == null && options == null)
        ""
    else
        "INSTALL_K3S_EXEC=\"$tlsSanOption ${options ?: ""}\""

    aptInstall("curl")
    if (!chk("k3s -version")) {
        if (docker) {
            // might not work if docker already installed
            sh("""
                curl https://releases.rancher.com/install-docker/19.03.sh | sh
                curl -sfL https://get.k3s.io | $k3sAllOptions sh -s - --docker
            """.trimIndent())
        } else {
            cmd("curl -sfL https://get.k3s.io | $k3sAllOptions sh -")
        }
    } else {
        ProvResult(true)
    }
}


@Suppress("unused")
fun Prov.uninstallK3sServer() = task {
    cmd("sudo /usr/local/bin/k3s-uninstall.sh")
}


fun Prov.applyK3sConfig(configAsYaml: String) = task {
    cmd(echoCommandForText(configAsYaml) + " | sudo k3s kubectl apply -f -")
}
