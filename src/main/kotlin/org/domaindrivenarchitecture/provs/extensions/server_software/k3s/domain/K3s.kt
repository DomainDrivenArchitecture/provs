package org.domaindrivenarchitecture.provs.extensions.server_software.k3s

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.ProvResult
import org.domaindrivenarchitecture.provs.core.echoCommandForText
import org.domaindrivenarchitecture.provs.ubuntu.install.base.aptInstall


/**
 * Installs a k3s server.
 * If docker is true, then k3s will be installed with docker option and also docker will be installed (may conflict if docker is already existing).
 * If host is specified, then tls (if configured) also applies to host.
 */
fun Prov.installK3sServer(docker: Boolean = false, host: String? = null) = task {
    val tls = host?.let { "INSTALL_K3S_EXEC=\"--tls-san ${it}\"" } ?: ""
    aptInstall("curl")
    if (!chk("k3s -version")) {
        if (docker) {
            // might not work if docker already installed
            sh("""
                curl https://releases.rancher.com/install-docker/19.03.sh | sh
                curl -sfL https://get.k3s.io | sh -s - --docker
            """.trimIndent())
        } else {
            cmd("curl -sfL https://get.k3s.io | $tls sh -")
        }
    } else {
        ProvResult(true)
    }
}


fun Prov.uninstallK3sServer() = task {
    cmd("sudo /usr/local/bin/k3s-uninstall.sh")
}


fun Prov.applyK3sConfig(configAsYaml: String) = task {
    cmd(echoCommandForText(configAsYaml) + " | sudo k3s kubectl apply -f -")
}


