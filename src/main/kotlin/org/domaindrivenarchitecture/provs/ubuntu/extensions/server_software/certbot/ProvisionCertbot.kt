package org.domaindrivenarchitecture.provs.ubuntu.extensions.server_software.certbot

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.ProvResult
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.fileExists
import org.domaindrivenarchitecture.provs.ubuntu.install.base.aptInstall


/**
 * Provisions a certbot for the specified serverName and email to obtain and renew letsencrypt certificates
 * Parameter can be used to specify certbot options e.g. "--nginx" to configure nginx, see https://certbot.eff.org/docs/using.html#certbot-command-line-options
 */
fun Prov.provisionCertbot(serverName: String, email: String?, additionalOptions: String? = "") = requireAll {
    aptInstall("snapd")
    sh("""
        sudo snap install core; sudo snap refresh core
        sudo snap install --classic certbot
    """.trimIndent())

    if (!fileExists("/usr/bin/certbot")) {
        cmd("sudo ln -s /snap/bin/certbot /usr/bin/certbot")
        val emailOption = email?.let { " -m $it" } ?: "--register-unsafely-without-email"
        cmd("sudo certbot $additionalOptions -n --agree-tos $emailOption -d $serverName")
    } else {
        ProvResult(true)
    }

}
