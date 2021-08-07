package io.provs.ubuntu.extensions.server_software.nginx.base

import io.provs.Prov
import io.provs.Secret
import io.provs.ubuntu.install.base.aptInstall

fun Prov.nginxAddBasicAuth(user: String, password: Secret) = requireAll {
    aptInstall("apache2-utils")
    val passwordFile = "/etc/nginx/.htpasswd"
    cmdNoLog("sudo htpasswd -b -c $passwordFile $user ${password.plain()}")
}

