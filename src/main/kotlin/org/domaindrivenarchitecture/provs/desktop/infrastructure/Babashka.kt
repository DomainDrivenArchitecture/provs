package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.*
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.checkCommand
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.base.downloadFromURL

fun Prov.installBabashka(
    version: String = "1.12.196",
    enforceUpgrade: Boolean = false,
    sha256sum: String = "18dbf47c79cc136fe9903642a7b0c9ab75f52282984197855b489b80469b8d8f"
) = taskWithResult {
    if (checkCommand("bb") && !enforceUpgrade) {
        return@taskWithResult ProvResult(true)
    }

    if (checkBabashkaVersion(version)) {
        return@taskWithResult ProvResult(true, info = "Babashka $version is already installed.")
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
        cmd("tar -C /usr/local/bin/ -xzf $target/babashka-$version-linux-amd64.tar.gz --no-same-owner", sudo = true)
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
    val result = cmdNoEval("/usr/local/bin/bb version")
    return if (!result.success) null else result.out
}