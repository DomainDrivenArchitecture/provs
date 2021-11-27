package org.domaindrivenarchitecture.provs.extensions.server_software.standalone_server.nginx.base

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.Secret
import org.domaindrivenarchitecture.provs.ubuntu.install.base.aptInstall

fun Prov.nginxAddBasicAuth(user: String, password: Secret) = requireAll {
    aptInstall("apache2-utils")
    val passwordFile = "/etc/nginx/.htpasswd"
    cmdNoLog("sudo htpasswd -b -c $passwordFile $user ${password.plain()}")
}

