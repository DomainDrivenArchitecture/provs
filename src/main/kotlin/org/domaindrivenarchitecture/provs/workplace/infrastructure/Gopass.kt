package org.domaindrivenarchitecture.provs.workplace.infrastructure

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.ProvResult
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.*
import org.domaindrivenarchitecture.provs.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.ubuntu.install.base.isPackageInstalled
import org.domaindrivenarchitecture.provs.ubuntu.web.base.downloadFromURL


fun Prov.installGopass(
    version: String = "1.12.7",
    enforceVersion: Boolean = false,
    sha256sum: String = "0824d5110ff1e68bff1ba10c1be63acb67cb1ad8e3bccddd6b6fc989608beca8" // checksum for sha256sum version 8.30 (e.g. ubuntu 20.04)
) = def {

    if (isPackageInstalled("gopass") && !enforceVersion) {
        return@def ProvResult(true)
    }
    if (checkGopassVersion(version)) {
        return@def ProvResult(true, out = "Version $version of gopass is already installed.")
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
        addResultToEval(ProvResult(checkGopassVersion(version)))
    } else {
        addResultToEval(ProvResult(false, err = "Gopass could not be installed. " + result.err))
    }
}


fun Prov.configureGopass(gopassRootFolder: String? = null) = def {
    val defaultRootFolder = userHome() + ".password-store"
    val rootFolder = gopassRootFolder ?: defaultRootFolder
    // use default
    createDir(rootFolder)
    createDirs(".config/gopass")
    createFile("~/.config/gopass/config.yml", gopassConfig(rootFolder))
}


fun Prov.gopassMountStore(storeName: String, path: String, indexOfRecepientKey: Int = 0) = def {
    cmd("printf \"$indexOfRecepientKey\\n\" | gopass mounts add $storeName $path")
}


internal fun gopassConfig(gopassRoot: String): String {
    return """
root:
  askformore: false
  autoclip: true
  autoprint: false
  autoimport: true
  autosync: false
  check_recipient_hash: false
  cliptimeout: 45
  concurrency: 1
  editrecipients: false
  exportkeys: true
  nocolor: false
  noconfirm: true
  nopager: false
  notifications: true
  path: gpgcli-gitcli-fs+file://$gopassRoot
  recipient_hash:
    .gpg-id: 3078303637343130344341383141343930350aa69f73cca23a9ac5c8b567dc185a756e97c982164fe25859e0d1dcc1475c80a615b2123af1f5f94c11e3e9402c3ac558f500199d95b6d3e301758586281dcd26
  safecontent: false
  usesymbols: false
mounts: {}
    """.trim() + "\n"
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