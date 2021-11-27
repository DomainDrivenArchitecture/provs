package org.domaindrivenarchitecture.provs.extensions.server_software.standalone_server.nexus

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.ProvResult
import org.domaindrivenarchitecture.provs.core.docker.containerRuns
import org.domaindrivenarchitecture.provs.core.remote
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.fileExists
import org.domaindrivenarchitecture.provs.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.ubuntu.user.base.createUser
import org.domaindrivenarchitecture.provs.extensions.server_software.standalone_server.certbot.provisionCertbot
import org.domaindrivenarchitecture.provs.extensions.server_software.nginx.base.NginxConf
import org.domaindrivenarchitecture.provs.extensions.server_software.nginx.base.nginxReverseProxyHttpConfig
import org.domaindrivenarchitecture.provs.extensions.server_software.nginx.provisionNginxStandAlone


/**
 * Provisions sonatype nexus in a docker container.
 * If you would want nexus to be accessible directly from the internet (e.g. for test or demo reasons)
 * set parameter portAccessibleFromNetwork to true.
 */
fun Prov.provisionNexusWithDocker(portAccessibleFromNetwork: Boolean = false) = requireAll {
    // https://blog.sonatype.com/sonatype-nexus-installation-using-docker
    // https://medium.com/@AhGh/how-to-setup-sonatype-nexus-3-repository-manager-using-docker-7ff89bc311ce
    aptInstall("docker.io")

    if (!containerRuns("nexus")) {
        val volume = "nexus-data"
        if (!cmdNoEval("docker volume inspect $volume").success) {
            cmd("docker volume create --name $volume")
        }
        cmd("sudo docker run -d --restart unless-stopped -p 8081:8081 --name nexus -v nexus-data:/nexus-data sonatype/nexus3")

        for (n in 0..3) {
            if (fileExists("/var/lib/docker/volumes/$volume/_data/admin.password", sudo = true)) {
                val res = cmd("sudo cat /var/lib/docker/volumes/$volume/_data/admin.password")
                println("Admin Password:" + res.out)
                break
            }
            Thread.sleep(20000)
        }
    }
    if (!portAccessibleFromNetwork) {
        val netIf = getDefaultNetworkingInterface()
        netIf?.also {
            val iptablesParameters = "DOCKER-USER -i $it ! -s 127.0.0.1 -j DROP"
            if (!cmdNoEval("sudo iptables -C $iptablesParameters").success) {
                cmd("sudo iptables -I $iptablesParameters")
            }
        }
    }
    ProvResult(true) // dummy
}

private fun Prov.getDefaultNetworkingInterface(): String? {
    return cmd("route | grep \"^default\" | grep -o \"[^ ]*\$\"\n").out?.trim()
}


/**
 * Provisions sonatype nexus on the specified host.
 * Creates user "nexus" on the remote system.
 * Installs nexus in a docker container behind an nginx reverse proxy with ssl using letsencrypt certificates.
 *
 * To run this method it is required to have ssl root access to the host.
 */
@Suppress("unused") // to be used externally
fun provisionNexusServer(serverName: String, certbotEmail: String) {
    val userName = "nexus" + 7
    remote(serverName, "root").def {
        createUser(userName, copyAuthorizedSshKeysFromCurrentUser = true, sudo = true)
    }
    remote(serverName, userName).requireAll {
        provisionNexusWithDocker()

        if (provisionNginxStandAlone(NginxConf.nginxReverseProxyHttpConfig(serverName)).success) {

            cmd("sudo cat /etc/nginx/nginx.conf")

            provisionCertbot(serverName, certbotEmail, "--nginx")

            optional {
                cmd("sudo cat /etc/nginx/nginx.conf")
                cmd("sudo sed -i 's/X-Forwarded-Proto \"http\"/X-Forwarded-Proto \"https\"/g' /etc/nginx/nginx.conf")
                cmd("sudo systemctl reload nginx")
            }
        } else {
            ProvResult(true)
        }
    }
}


