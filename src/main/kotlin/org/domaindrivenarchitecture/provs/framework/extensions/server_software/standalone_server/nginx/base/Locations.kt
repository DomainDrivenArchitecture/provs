package org.domaindrivenarchitecture.provs.framework.extensions.server_software.standalone_server.nginx.base

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDirs
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.replaceTextInFile


internal const val locationsAvailableDir = "/etc/nginx/locations-available/"
internal const val locationsEnabledDir = "/etc/nginx/locations-enabled/"
internal const val locationsFileExtension = ".locations"


fun Prov.createNginxLocationFolders() = task {
    createDirs(locationsEnabledDir, sudo = true)
    createDirs(locationsAvailableDir, sudo = true)
}


fun Prov.nginxIncludeLocationFolders() = task {
    replaceTextInFile("/etc/nginx/nginx.conf", "listen 80;\n",
        """listen 80;
            include ${locationsAvailableDir}port80*$locationsFileExtension;
            include ${locationsEnabledDir}port443*$locationsFileExtension;
            """)
}
