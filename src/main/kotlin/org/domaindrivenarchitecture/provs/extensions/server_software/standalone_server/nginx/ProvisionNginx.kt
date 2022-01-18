package org.domaindrivenarchitecture.provs.extensions.server_software.standalone_server.nginx

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.core.remote
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.createFile
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.fileExists
import org.domaindrivenarchitecture.provs.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.extensions.server_software.standalone_server.nginx.base.NginxConf
import org.domaindrivenarchitecture.provs.extensions.server_software.standalone_server.nginx.base.createNginxLocationFolders
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