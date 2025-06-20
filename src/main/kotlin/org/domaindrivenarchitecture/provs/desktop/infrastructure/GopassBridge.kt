package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.*
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.infrastructure.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.infrastructure.checkPackage
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.infrastructure.downloadFromURL


fun Prov.downloadGopassBridge() = task {
    // Attention: when changing the version, you also need to change the number after /file/ in the download url below
    val filename = "gopass_bridge-0.9.0-fx.xpi"
    val downloadDir = "${userHome()}Downloads/"

    createDirs(downloadDir)
    downloadFromURL(
        "-L https://addons.mozilla.org/firefox/downloads/file/3630534/$filename",
        downloadDir + filename
    )
    // needs manual installation with: firefox Downloads/gopass_bridge-0.X.0-fx.xpi
}

fun Prov.installGopassJsonApi() = taskWithResult {
    // from https://github.com/gopasspw/gopass-jsonapi/releases/tag/v1.15.13
    val sha256sum = "3162ab558301645024325ce2e419c1d67900e1faf95dc1774a36f1ebfc76389f"
    val gopassJsonApiVersion = "1.15.13"
    val requiredGopassVersion = "1.15.13"
    val filename = "gopass-jsonapi_${gopassJsonApiVersion}_linux_amd64.deb"
    val downloadUrl = "-L https://github.com/gopasspw/gopass-jsonapi/releases/download/v$gopassJsonApiVersion/$filename"
    val downloadDir = "${userHome()}Downloads"
    val installedJsonApiVersion = gopassJsonApiVersion()?.trim()

    if (installedJsonApiVersion == null) {
        if (chk("gopass ls")) {
            if (checkGopassVersion(requiredGopassVersion)) {
                aptInstall("git gnupg2")   // required dependencies
                createDir(downloadDir)
                downloadFromURL(downloadUrl, filename, downloadDir, sha256sum = sha256sum)
                cmd("dpkg -i $downloadDir/$filename", sudo = true)
            } else {
                ProvResult(
                    false,
                    "Version of currently installed gopass (" + gopassVersion() + ") is incompatible with gopass-jsonapi version to be installed. " +
                            "Please upgrade gopass to version: " + requiredGopassVersion
                )
            }
        } else {
            ProvResult(
                false,
                "gopass not initialized correctly. You can initialize gopass with: \"gopass init\""
            )
        }
    } else {
        if (installedJsonApiVersion.startsWith("gopass-jsonapi version $gopassJsonApiVersion")) {
            ProvResult(true, info = "Gopass-jsonapi $gopassJsonApiVersion is already installed")
        } else {
            ProvResult(
                false,
                err = "gopass-jsonapi (version $gopassJsonApiVersion) cannot be installed as version $installedJsonApiVersion is already installed." +
                        " Upgrading gopass-jsonapi is currently not supported by provs."
            )
        }
    }
}

/**
 * Configures apparmor to allow firefox to access to gopass_wrapper.sh in avoid
 * the error "An unexpected error occurred - Is your browser correctly set up for gopass? ..."
 * when trying to use gopass bridge.
 * This error appears in spite of having already set up gopass-jsonapi correctly.
 */
fun Prov.configureApparmorForGopassWrapperShForFirefox() = task {

    val appArmorFile = "/etc/apparmor.d/usr.bin.firefox"
    val gopassAccessPermission = "owner @{HOME}/.config/gopass/gopass_wrapper.sh Ux,"
    val insertAfterText = "# per-user firefox configuration\n"

    if (checkFile(appArmorFile) && !fileContainsText(appArmorFile, gopassAccessPermission, true)) {
        replaceTextInFile(
            appArmorFile, insertAfterText, "$insertAfterText  $gopassAccessPermission\n"
        )
        cmd("systemctl reload apparmor", sudo = true)
    }
}

fun Prov.configureGopassJsonApi() = taskWithResult {
    if (checkPackage("gopass-jsonapi")) {
        // configures gopass-jsonapi for firefox and chooses default for each:
        // * "Install for all users? [y/N/q]",
        // * "In which path should gopass_wrapper.sh be installed? [/home/<user>/.config/gopass]"
        // * "Wrapper Script for gopass_wrapper.sh ..."
        //
        // I.e. creates file "gopass_wrapper.sh" in "/home/<user>/.config/gopass" as well as
        // the manifest file "/home/<user>/.mozilla/native-messaging-hosts/com.justwatch.gopass.json"
        cmd("printf \"\\n\\n\\n\" | gopass-jsonapi configure --browser firefox")

        configureApparmorForGopassWrapperShForFirefox()
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
