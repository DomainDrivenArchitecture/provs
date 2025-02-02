package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.*
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.checkCommand
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.base.downloadFromURL

//from https://go.dev/dl/
fun Prov.installGo(
    version: String = "1.23.5",
    enforceVersion: Boolean = false,
    sha256sum: String = "cbcad4a6482107c7c7926df1608106c189417163428200ce357695cc7e01d091"
) = taskWithResult {
    if (checkCommand("go") && !enforceVersion) {
        return@taskWithResult ProvResult(true)
    }

    if (checkGoVersion(version)) {
        return@taskWithResult ProvResult(true, out = "Version $version of go is already installed.")
    }

    val downloadUrl = "https://go.dev/dl/go$version.linux-amd64.tar.gz"
    val filename = "go$version.linux-amd64.tar.gz"
    val target = "${userHome()}tmp"
    val result = downloadFromURL(
        downloadUrl,
        filename,
        target,
        sha256sum = sha256sum
    )

    if (result.success) {
        cmd("tar -C /usr/local -xzf $target/go1.23.5.linux-amd64.tar.gz", sudo = true)
        deleteFile("$target/$filename")
        configureBashForUser()
        createFile("~/.bashrc.d/go.sh", "export PATH=\$PATH:/usr/local/go/bin\n")
        // check and assert installation
        addResult(checkGoVersion(version), info = "Go version $version has been installed.")
    } else {
        return@taskWithResult ProvResult(false, err = "Go $version could not be downloaded and installed. " + result.err)
    }
}

fun Prov.checkGoVersion(version: String): Boolean {
    val installedGoVersion = goVersion()
    return installedGoVersion != null && installedGoVersion.startsWith("go version go" + version)
}

internal fun Prov.goVersion(): String? {
    val result = cmdNoEval("/usr/local/go/bin/go version")
    return if (!result.success) null else result.out
}