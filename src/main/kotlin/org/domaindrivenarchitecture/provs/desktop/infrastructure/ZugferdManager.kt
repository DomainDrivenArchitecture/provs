package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.createDir
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.userHome
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.infrastructure.checkPackage
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.infrastructure.downloadFromURL

fun Prov.installZugferdManager(
    version: String = "1.3.2",
    reInstall: Boolean = false
) = task {

    if (checkPackage("zugferd-manager") && !reInstall) {
        val versionInst = cmdNoEval("dpkg-query -W zugferd-manager").out?.trim()
        addResult(true, info = "$versionInst is already installed.")
    }

    val downloadUrl =
        "https://github.com/OpenIndex/ZUGFeRD-Manager/releases/download/v$version/ZUGFeRD-Manager-$version-linux-x64.deb"
    val filename = "ZUGFeRD-Manager-${version}-linux-x64.deb"
    val target = "${userHome()}tmp"
    createDir("tmp")

    val result = downloadFromURL(
        downloadUrl,
        filename,
        target
    )

    if (result.success) {
        cmd("apt-get install -fqy $target/$filename > tmp/zugferd-manager-install.log", sudo = true)
        addResult(checkZugferdVersion(version), info = "Zugferd-Manager version $version has been installed.")
    }
}

fun Prov.checkZugferdVersion(version: String): Boolean {
    val installedZugferdVersion = zugferdVersion()
    return installedZugferdVersion != null && installedZugferdVersion.contains(version)
}

internal fun Prov.zugferdVersion(): String? {
    val result = cmdNoEval("dpkg-query -W zugferd-manager")
    return if (!result.success) null else result.out
}
