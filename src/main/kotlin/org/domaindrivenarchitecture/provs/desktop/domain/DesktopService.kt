package org.domaindrivenarchitecture.provs.desktop.domain

import org.domaindrivenarchitecture.provs.desktop.domain.DesktopOnlyModule.FIREFOX
import org.domaindrivenarchitecture.provs.desktop.domain.DesktopOnlyModule.VERIFY
import org.domaindrivenarchitecture.provs.desktop.infrastructure.*
import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.git.domain.provisionGit
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.infrastructure.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.infrastructure.aptPurge
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.domain.KeyPair
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.domain.SshKeyPair
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.infrastructure.gpgFingerprint
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.domain.provisionKeys
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.infrastructure.currentUserCanSudoWithoutPassword
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.infrastructure.whoami


internal fun Prov.provisionDesktopCommand(cmd: DesktopCliCommand, conf: DesktopConfig) = task {

    validatePrecondition()

    val only = cmd.onlyModules
    if (only == null) {
        provisionDesktop(
            cmd.type,
            conf.ssh?.keyPair(),
            conf.gpg?.keyPair(),
            conf.gitUserName,
            conf.gitEmail,
        )
    } else {
        provisionOnlyModules(cmd.type, only)
    }
}


/**
 * Provisions software and configurations for a personal desktop.
 * Offers the possibility to choose between different types.
 * Type OFFICE installs office-related software like Thunderbird, LibreOffice, and much more.
 * Type IDE provides additional software for a development environment, such as Visual Studio Code, IntelliJ, etc.
 *
 * Prerequisites: user must be able to sudo without entering the password
 */
internal fun Prov.provisionDesktop(
    desktopType: DesktopType = DesktopType.BASIC,
    ssh: SshKeyPair? = null,
    gpg: KeyPair? = null,
    gitUserName: String? = null,
    gitEmail: String? = null,
) = task {

    provisionBasicDesktop(gpg, ssh, gitUserName, gitEmail)

    if (desktopType == DesktopType.OFFICE) {
        provisionOfficeDesktop()
        verifyOfficeSetup()
    }
    if (desktopType == DesktopType.IDE) {
        provisionOfficeDesktop()
        provisionIdeDesktop()
        verifyIdeSetup()
    }
}

internal fun Prov.provisionOnlyModules(
    desktopType: DesktopType = DesktopType.BASIC,
    onlyModules: List<String>
) = task {

    if (FIREFOX.isIn(onlyModules)) {
        installPpaFirefox()
    }
    if (VERIFY.isIn(onlyModules)) {
        if (desktopType == DesktopType.OFFICE) {
            verifyOfficeSetup()
        } else if (desktopType == DesktopType.IDE) {
            verifyIdeSetup()
        }
    }
}

fun Prov.validatePrecondition() {
    if (!currentUserCanSudoWithoutPassword()) {
        throw Exception("Current user ${whoami()} cannot execute sudo without entering a password! This is necessary to execute provisionDesktop")
    }
}


fun Prov.provisionBasicDesktop(
    gpg: KeyPair?,
    ssh: SshKeyPair?,
    gitUserName: String?,
    gitEmail: String?,
) {
    aptInstall(KEY_MANAGEMENT)
    aptInstall(VERSION_MANAGEMENT)
    aptInstall(NETWORK_TOOLS)
    aptInstall(SCREEN_TOOLS)
    aptInstall(KEY_MANAGEMENT_GUI)
    aptInstall(PASSWORD_TOOLS)
    aptInstall(OS_ANALYSIS)
    aptInstall(BASH_UTILS)
    aptInstall(CLIP_TOOLS)
    aptPurge(
        "remove-power-management xfce4-power-manager " +
                "xfce4-power-manager-plugins xfce4-power-manager-data " +
                "upower libimobiledevice6 libplist3 libusbmuxd6 usbmuxd bluez-cups"
    )
    aptPurge("abiword gnumeric")
    aptPurge("popularity-contest")

    provisionKeys(gpg, ssh)
    provisionGit(gitUserName ?: whoami(), gitEmail, gpg?.let { gpgFingerprint(it.publicKey.plain()) })

    installPpaFirefox()
    installGopass()
    configureGopass(publicGpgKey = gpg?.publicKey)
    installGopassJsonApi()
    downloadGopassBridge()

    installRedshift()
    configureRedshift()

    configureNoSwappiness()
    configureBash()
    installVirtualBoxGuestAdditions()
}

fun Prov.provisionOfficeDesktop() {
    aptInstall(ZIP_UTILS)
    aptInstall(SPELLCHECKING_DE)
    aptInstall(BROWSER)
    aptInstall(EMAIL_CLIENT)
    installDeltaChat()
    aptInstall(OFFICE_SUITE)
    installZimWiki()
    installZugferdManager()

    aptInstall(COMPARE_TOOLS)

    // VSCode is also required in office VM (not only in IDE desktop) e.g. as editor
    installVSCode("python", "clojure")

    // optional, as installation of these tools often fail and as they are not mandatory
    optional {
        aptInstall(DRAWING_TOOLS)
    }
}


fun Prov.provisionIdeDesktop() {
    aptInstall(OPEN_VPM)
    aptInstall(OPENCONNECT)
    aptInstall(VPNC)

    // DevEnvs
    installDocker()
    aptInstall(JAVA)
    aptInstall(CLOJURE_TOOLS)
    installNpmByNvm()
    installShadowCljs()
    installDevOps()
    provisionPython()
    installHugoByDeb()
    installGo()
    installBabashka()
    installTerragrunt()
    installOpentofu()
    installDirenv()

    // IDEs
    installIntelliJ()

    installKubeconform()
}
