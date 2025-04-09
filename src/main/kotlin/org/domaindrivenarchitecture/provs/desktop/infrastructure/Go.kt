package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.*
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.checkCommand
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.base.downloadFromURL

//from https://go.dev/dl/
fun Prov.installGo(
    version: String = "1.24.2",
    enforceUpgrade: Boolean = false,
    sha256sum: String = "68097bd680839cbc9d464a0edce4f7c333975e27a90246890e9f1078c7e702ad"
) = taskWithResult {
    if (checkCommand("go") && !enforceUpgrade) {
        return@taskWithResult ProvResult(true)
    }

    if (checkGoVersion(version)) {
        return@taskWithResult ProvResult(true, out = "Go $version is already installed.")
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
        cmd("tar -C /usr/local -xzf $target/go1.24.2.linux-amd64.tar.gz", sudo = true)
        deleteFile("$target/$filename")
        configureBashForUser()
        val bashConfigFile = "~/.bashrc.d/go.sh"
        val content = "export PATH=\$PATH:/usr/local/go/bin\nexport PATH=\$PATH:\$HOME/go/bin\n"
        createFile(bashConfigFile, content)
        // check and assert installation
        addResult(checkGoVersion(version), info = "Go $version has been installed.")
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