package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.*
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.isPackageInstalled
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base.gpgFingerprint
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.base.downloadFromURL


fun Prov.installGopass(
    version: String = "1.12.7",
    enforceVersion: Boolean = false,
    sha256sum: String = "0824d5110ff1e68bff1ba10c1be63acb67cb1ad8e3bccddd6b6fc989608beca8" // checksum for sha256sum version 8.30 (e.g. ubuntu 20.04)
) = taskWithResult {

    if (isPackageInstalled("gopass") && !enforceVersion) {
        return@taskWithResult ProvResult(true)
    }
    if (checkGopassVersion(version)) {
        return@taskWithResult ProvResult(true, out = "Version $version of gopass is already installed.")
    }

    val path = "tmp"
    // install required dependencies
    aptInstall("rng-tools gnupg2 git")
    val filename = "gopass_${version}_linux_amd64.deb"
    val result = downloadFromURL(
        "https://github.com/gopasspw/gopass/releases/download/v$version/$filename",
        filename,
        path,
        sha256sum = sha256sum
    )
    if (result.success) {
        cmd("sudo dpkg -i $path/gopass_${version}_linux_amd64.deb")
        // Cross-check if installation was successful
        return@taskWithResult ProvResult(checkGopassVersion(version))
    } else {
        return@taskWithResult ProvResult(false, err = "Gopass could not be installed. " + result.err)
    }
}


fun Prov.configureGopass(gopassRootFolder: String? = null, publicGpgKey: Secret? = null) = taskWithResult {

    val configFile = ".config/gopass/config.yml"

    if (checkFile(configFile)) {
        return@taskWithResult ProvResult(true, out = "Gopass already configured in file $configFile")
    }

    if ((gopassRootFolder != null) && (!gopassRootFolder.startsWith("/"))) {
        return@taskWithResult ProvResult(false, err = "Gopass cannot be initialized with a relative path or path starting with ~ ($gopassRootFolder)")
    }

    val defaultRootFolder = userHome() + ".password-store"
    val gopassRoot = gopassRootFolder ?: defaultRootFolder

    // initialize root store
    val fingerprint = publicGpgKey?.let { gpgFingerprint(it.plain()) }
    gopassInitStoreFolder(gopassRoot, fingerprint)

    createDirs(".config/gopass")
    createFile(configFile, gopassConfig(gopassRoot))

    // auto-completion
    configureBashForUser()
    createFile("~/.bashrc.d/gopass.sh", "source <(gopass completion bash)\n")
}


fun Prov.gopassMountStore(storeName: String, path: String) = task {
    cmd("gopass mounts add $storeName $path")
}


fun Prov.gopassInitStoreFolder(path: String, gpgFingerprint: String? = null ) = task {
    createFile("$path/.gpg-id", gpgFingerprint ?: "_replace_this_by_a_fingerprint_of_a_public_gpg_key_")
}


internal fun gopassConfig(gopassRoot: String): String {
    return """
        autoclip: true
        autoimport: true
        cliptimeout: 45
        exportkeys: true
        nocolor: false
        nopager: false
        notifications: true
        parsing: true
        path: $gopassRoot
        safecontent: false
        mounts: {}
    """.trimIndent() + "\n"
}


/**
 * Returns true if gopass is installed and has the given version.
 *
 * @param version that is checked; specifies left part of text of installed version, e.g. both "1" and "1.12" will return true if installed version is "1.12.6+8d7a311b9273846bbb618e4bd9ddbae51b1db7b8"
 */
internal fun Prov.checkGopassVersion(version: String): Boolean {
    val installedGopassVersion = gopassVersion()
    return installedGopassVersion != null && installedGopassVersion.startsWith("gopass " + version)
}

internal fun Prov.gopassVersion(): String? {
    val result = cmdNoEval("gopass -v")
    return if (!result.success) null else result.out
}
