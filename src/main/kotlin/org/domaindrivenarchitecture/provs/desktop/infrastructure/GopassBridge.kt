package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDir
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDirs
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.userHome
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.isPackageInstalled
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.base.downloadFromURL


fun Prov.downloadGopassBridge() = def {
    val version = "0.8.0"
    val filename = "gopass_bridge-${version}-fx.xpi"
    val downloadDir = "${userHome()}Downloads/"

    createDirs(downloadDir)
    downloadFromURL(
        "-L https://addons.mozilla.org/firefox/downloads/file/3630534/" + filename,
        downloadDir + filename
    )
    // needs manual install with: firefox Downloads/gopass_bridge-0.8.0-fx.xpi
}

fun Prov.installGopassBridgeJsonApi() = def {
    // see https://github.com/gopasspw/gopass-jsonapi
    val gopassBridgeVersion = "1.11.1"
    val requiredGopassVersion = "1.12"
    val filename = "gopass-jsonapi_${gopassBridgeVersion}_linux_amd64.deb"
    val downloadUrl = "-L https://github.com/gopasspw/gopass-jsonapi/releases/download/v$gopassBridgeVersion/$filename"
    val downloadDir = "${userHome()}Downloads"
    val installedJsonApiVersion = gopassJsonApiVersion()?.trim()

    if (installedJsonApiVersion == null) {
        if (chk("gopass ls")) {
            if (checkGopassVersion(requiredGopassVersion)) {
                aptInstall("git gnupg2")   // required dependencies
                createDir(downloadDir)
                downloadFromURL(downloadUrl, filename, downloadDir)
                cmd("dpkg -i " + downloadDir + "/" + filename, sudo = true)
            } else {
                ProvResult(
                    false,
                    "Version of currently installed gopass (" + gopassVersion() + ") is incompatible with gopass-jsonapi version to be installed. " +
                            "Please upgrade gopass to version: " + requiredGopassVersion
                )
            }
        } else {
            addResultToEval(
                ProvResult(
                    false,
                    "gopass not initialized correctly. You can initialize gopass with: \"gopass init\""
                )
            )
        }
    } else {
        if (installedJsonApiVersion.startsWith("gopass-jsonapi version " + gopassBridgeVersion)) {
            addResultToEval(ProvResult(true, out = "Version $gopassBridgeVersion of gopass-jsonapi is already installed"))
        } else {
            addResultToEval(
                ProvResult(
                    false,
                    err = "gopass-jsonapi (version $gopassBridgeVersion) cannot be installed as version $installedJsonApiVersion is already installed." +
                            " Upgrading gopass-jsonapi is currently not supported by provs."
                )
            )
        }
    }
}

fun Prov.configureGopassBridgeJsonApi() = def {
    if (isPackageInstalled("gopass-jsonapi")) {
        // configure for firefox and choose default for each:
        // "Install for all users? [y/N/q]",
        // "In which path should gopass_wrapper.sh be installed? [/home/testuser/.config/gopass]"
        // "Wrapper Script for gopass_wrapper.sh ..."
        cmd("printf \"\\n\\n\\n\" | gopass-jsonapi configure --browser firefox")
    } else {
        ProvResult(
            false,
            err = "gopass-jsonapi is missing. Gopass-jsonapi must be installed to be able to configure it."
        )
    }
}

internal fun Prov.gopassJsonApiVersion(): String? {
    val result = cmdNoEval("gopass-jsonapi -v")
    return if (!result.success) null else result.out
}
