package org.domaindrivenarchitecture.provs.ubuntu.extensions.workplace.base

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.ProvResult
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.createDir
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.createDirs
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.createFile
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.userHome
import org.domaindrivenarchitecture.provs.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.ubuntu.install.base.isPackageInstalled


fun Prov.installGopass(version: String = "1.12.7", enforceVersion: Boolean = false) = def {
    if (isPackageInstalled("gopass") && !enforceVersion) {
        ProvResult(true)
    } else {
        // install required dependencies
        aptInstall("rng-tools gnupg2 git")
        aptInstall("curl")

        sh(
            """
            curl -L https://github.com/gopasspw/gopass/releases/download/v${version}/gopass_${version}_linux_amd64.deb -o gopass_${version}_linux_amd64.deb
            sudo dpkg -i gopass_${version}_linux_amd64.deb
        """
        )
        gopassEnsureVersion(version)
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
internal fun Prov.gopassEnsureVersion(version: String) = def {
    val installedGopassVersion = gopassVersion()
    if (installedGopassVersion != null && installedGopassVersion.startsWith("gopass " + version)) {
        ProvResult(true, out = "Required gopass version ($version) matches installed version ($installedGopassVersion)")
    } else {
        ProvResult(false, err = "Wrong gopass version. Expected $version but found $installedGopassVersion")
    }
}

internal fun Prov.gopassVersion(): String? {
    val result = cmdNoEval("gopass -v")
    return if (!result.success) null else result.out
}