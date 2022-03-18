package org.domaindrivenarchitecture.provs.framework.extensions.server_software.standalone_server.nginx

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.extensions.server_software.standalone_server.nginx.base.NginxConf
import org.domaindrivenarchitecture.provs.framework.extensions.server_software.standalone_server.nginx.base.createNginxLocationFolders
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall


internal const val NGINX_CONFIG_FILE = "/etc/nginx/nginx.conf"


fun Prov.provisionNginxStandAlone(config: NginxConf? = null) = task {

    aptInstall("nginx")

    createNginxLocationFolders()

    if (config != null) {
        if (checkFile(NGINX_CONFIG_FILE)) {
            cmd("sudo mv $NGINX_CONFIG_FILE $NGINX_CONFIG_FILE-orig")
        }
        createFile(NGINX_CONFIG_FILE, config.conf, sudo = true)
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
