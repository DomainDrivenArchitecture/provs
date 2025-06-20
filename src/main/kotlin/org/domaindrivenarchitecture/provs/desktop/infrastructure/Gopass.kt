package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.*
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.infrastructure.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.infrastructure.checkPackage
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.infrastructure.gpgFingerprint
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.infrastructure.downloadFromURL


fun Prov.installGopass(
    version: String = "1.15.13",  // NOTE: when adjusting, pls also adjust checksum below and version of gopass bridge json api
    enforceUpgrade: Boolean = false,
    // from https://github.com/gopasspw/gopass/releases/tag/v1.15.13
    sha256sum: String = "409ed5617e64fa2c781d5e2807ba7fcd65bc383a4e110f410f90b590e51aec55"
) = taskWithResult {

    if (checkPackage("gopass") && !enforceUpgrade) {
        return@taskWithResult ProvResult(true)
    }
    if (checkGopassVersion(version)) {
        return@taskWithResult ProvResult(true, out = "Gopass $version is already installed.")
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
        deleteFile("$path/$filename")
        return@taskWithResult ProvResult(checkGopassVersion(version))
    } else {
        deleteFile("$path/$filename")
        return@taskWithResult ProvResult(false, err = "Gopass could not be installed. " + result.err)
    }
}


fun Prov.configureGopass(gopassRootFolder: String? = null, publicGpgKey: Secret? = null) = taskWithResult {

    val configFile = ".config/gopass/config"

    if ((gopassRootFolder != null) && (!gopassRootFolder.startsWith("/"))) {
        return@taskWithResult ProvResult(false, err = "Gopass cannot be initialized with a relative path or path starting with ~ ($gopassRootFolder)")
    }

    if(!fileContainsText(configFile,"share/gopass/stores/root")){
        return@taskWithResult ProvResult(true, out = "Gopass already configured in file $configFile")
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


fun Prov.gopassMountStore(storeName: String, path: String) = taskWithResult {
    val mounts = cmdNoEval("gopass mounts").out ?: return@taskWithResult ProvResult(false, err = "could not determine gopass mounts")
    if (mounts.contains(storeName)) {
        ProvResult(true, out = "Store $storeName already mounted.")
    } else {
        cmd("gopass mounts add $storeName $path")
    }
}


fun Prov.gopassInitStoreFolder(path: String, gpgFingerprint: String? = null ) = task {
    createFile("$path/.gpg-id", gpgFingerprint ?: "_replace_this_by_a_fingerprint_of_a_public_gpg_key_")
    if (!checkDir(".git", path)) {
        cmd("git init", path)
    }
}


internal fun gopassConfig(gopassRoot: String): String {
    return """
    [core]
	    parsing = true
	    exportkeys = true
	    autoclip = true
	    showsafecontent = false
	    nopager = false
        cliptimeout = 45
        notifications = true
        autoimport = true
    [age]
	    usekeychain = false
    [mounts]
	    path = $gopassRoot
    """
    .trimIndent() + "\n"
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
