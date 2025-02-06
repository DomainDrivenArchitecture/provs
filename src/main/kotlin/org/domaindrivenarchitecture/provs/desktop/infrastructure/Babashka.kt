package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.*
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.checkCommand
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.base.downloadFromURL

fun Prov.installBabashka(
    version: String = "1.12.196",
    enforceVersion: Boolean = false,
    sha256sum: String = "13c197bf1151cac038abedfa2869a27303f62650474f334867264e13ee9f8cd6"
) = taskWithResult {
    if (checkCommand("bb") && !enforceVersion) {
        return@taskWithResult ProvResult(true)
    }

    if (checkBabashkaVersion(version)) {
        return@taskWithResult ProvResult(true, out = "Version $version of babashka is already installed.")
    }

    val downloadUrl = "https://github.com/babashka/babashka/releases/download/v$version/babashka-$version-linux-amd64.tar.gz"
    val filename = "babashka-$version-linux-amd64.tar.gz"
    val target = "${userHome()}tmp"
    val result = downloadFromURL(
        downloadUrl,
        filename,
        target,
        sha256sum = sha256sum
    )

    if (result.success) {
        cmd("tar -C /usr/local/bin/ -xzf $target/go1.23.5.linux-amd64.tar.gz --no-same-owner", sudo = true)
        deleteFile("$target/$filename")

        // check and assert installation
        addResult(checkBabashkaVersion(version), info = "Babashka version $version has been installed.")
    } else {
        return@taskWithResult ProvResult(false, err = "Babashka $version could not be downloaded and installed. " + result.err)
    }
}

fun Prov.checkBabashkaVersion(version: String): Boolean {
    val installedBabashkaVersion = babashkaVersion()
    return installedBabashkaVersion != null && installedBabashkaVersion.startsWith("babashka v" + version)
}

internal fun Prov.babashkaVersion(): String? {
    val result = cmdNoEval("/usr/local/bb version")
    return if (!result.success) null else result.out
}