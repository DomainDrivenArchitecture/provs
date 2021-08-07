package io.provs.ubuntu.extensions.server_software.nginx

import io.provs.core.Prov
import io.provs.core.ProvResult
import io.provs.core.remote
import io.provs.ubuntu.filesystem.base.createFile
import io.provs.ubuntu.filesystem.base.fileExists
import io.provs.ubuntu.install.base.aptInstall
import io.provs.ubuntu.extensions.server_software.nginx.base.NginxConf
import io.provs.ubuntu.extensions.server_software.nginx.base.createNginxLocationFolders
import kotlin.system.exitProcess


internal const val configFile = "/etc/nginx/nginx.conf"


fun Prov.provisionNginxStandAlone(config: NginxConf? = null) = requireAll {

    aptInstall("nginx")

    createNginxLocationFolders()

    if (config != null) {
        if (fileExists(configFile)) {
            cmd("sudo mv $configFile $configFile-orig")
        }
        createFile(configFile, config.conf, sudo = true)
        val configCheck = cmd("sudo nginx -t")
        if (configCheck.success) {
            cmd("sudo service nginx restart")
        } else {
            ProvResult(false, err = "Nginx config is incorrect:\n" + configCheck.err)
        }
    } else {
        ProvResult(true) // dummy
    }
}


fun provisionRemote(vararg args: String) {
    if (args.size != 2) {
        println("Pls specify host and user for remote installation of nginx.")
        exitProcess(1)
    }
    remote(args[0], args[1]).provisionNginxStandAlone()
}