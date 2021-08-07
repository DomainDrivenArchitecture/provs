package org.domaindrivenarchitecture.provs.extensions.server_software.nginx.base

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.ProvResult
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.*


internal const val locationsAvailableDir = "/etc/nginx/locations-available/"
internal const val locationsEnabledDir = "/etc/nginx/locations-enabled/"
internal const val locationsFileExtension = ".locations"


fun Prov.createNginxLocationFolders() = requireAll {
    createDirs(locationsEnabledDir, sudo = true)
    createDirs(locationsAvailableDir, sudo = true)
}


fun Prov.nginxIncludeLocationFolders() = requireAll {
    replaceTextInFile("/etc/nginx/nginx.conf", "listen 80;\n",
        """listen 80;
            include ${locationsAvailableDir}port80*$locationsFileExtension;
            include ${locationsEnabledDir}port443*$locationsFileExtension;
            """)
}


fun Prov.nginxAddLocation(port: String, locationFileName: String, urlPath: String, content: String) = requireAll {

    val locationConf = """location $urlPath {""" +
            content +
            "\n}"

    if (!dirExists(locationsAvailableDir, sudo = true)) {
        createNginxLocationFolders()
    }

    createFile("${locationsAvailableDir}port${port}_$locationFileName$locationsFileExtension", locationConf, sudo = true)
    if (!fileExists("${locationsEnabledDir}port${port}_$locationFileName$locationsFileExtension", sudo = true)) {
        cmd("sudo ln -s ${locationsAvailableDir}port${port}_$locationFileName$locationsFileExtension ${locationsEnabledDir}port${port}_$locationFileName$locationsFileExtension ")
    } else {
      ProvResult(true)
    }
}
