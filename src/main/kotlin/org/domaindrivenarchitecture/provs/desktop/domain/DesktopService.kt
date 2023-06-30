package org.domaindrivenarchitecture.provs.desktop.domain

import org.domaindrivenarchitecture.provs.desktop.infrastructure.*
import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.git.provisionGit
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptPurge
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.KeyPair
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.SshKeyPair
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base.gpgFingerprint
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.provisionKeys
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.base.currentUserCanSudoWithoutPassword
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.base.whoami

internal fun Prov.provisionDesktopCommand(cmd: DesktopCliCommand, conf: DesktopConfig) = task {
    provisionDesktop(
        cmd.type,
        conf.ssh?.keyPair(),
        conf.gpg?.keyPair(),
        conf.gitUserName,
        conf.gitEmail,
        cmd.onlyModules
    )
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
    onlyModules: List<String>?
) = task {
    validatePrecondition()
    provisionBasicDesktop(gpg, ssh, gitUserName, gitEmail, onlyModules)

    if (desktopType == DesktopType.OFFICE) {
        provisionOfficeDesktop(onlyModules)
        if (onlyModules == null) {
            verifyOfficeSetup()
        }
    }
    if (desktopType == DesktopType.IDE) {
        if (onlyModules == null) {
            provisionOfficeDesktop()
            provisionIdeDesktop()
            verifyIdeSetup()
        } else {
            provisionIdeDesktop(onlyModules)
        }
    }
}

fun Prov.validatePrecondition() {
    if (!currentUserCanSudoWithoutPassword()) {
        throw Exception("Current user ${whoami()} cannot execute sudo without entering a password! This is necessary to execute provisionDesktop")
    }
}

fun Prov.provisionIdeDesktop(onlyModules: List<String>? = null) {
    if (onlyModules == null) {
        aptInstall(OPEN_VPM)
        aptInstall(OPENCONNECT)
        aptInstall(VPNC)

        // DevEnvs
        installDocker()
        aptInstall(JAVA)
        aptInstall(CLOJURE_TOOLS)
        installShadowCljs()
        installDevOps()
        provisionPython()

        // IDEs
        installVSC("python", "clojure")
        installIntelliJ()
    } else if (onlyModules.contains(DesktopOnlyModule.VERIFY.name.lowercase())) {
        verifyIdeSetup()
    } else if (onlyModules.contains(DesktopOnlyModule.FIREFOX.name.lowercase())) {
        installFirefox()
    }
}

fun Prov.provisionOfficeDesktop(onlyModules: List<String>? = null) {
    if (onlyModules == null) {
        aptInstall(ZIP_UTILS)
        aptInstall(SPELLCHECKING_DE)
        aptInstall(BROWSER)
        aptInstall(EMAIL_CLIENT)
        installDeltaChat()
        aptInstall(OFFICE_SUITE)
        installZimWiki()
        installNextcloudClient()
        aptInstall(COMPARE_TOOLS)

        // optional as installation of these tools often fail and they are not considered mandatory
        optional {
            aptInstall(DRAWING_TOOLS)
        }
    } else if (onlyModules.contains(DesktopOnlyModule.VERIFY.name.lowercase())) {
        verifyOfficeSetup()
    } else if (onlyModules.contains(DesktopOnlyModule.FIREFOX.name.lowercase())) {
        installFirefox()
    }
}

fun Prov.provisionBasicDesktop(
    gpg: KeyPair?,
    ssh: SshKeyPair?,
    gitUserName: String?,
    gitEmail: String?,
    onlyModules: List<String>?
) {
    if (onlyModules == null) {
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
                    "xfce4-power-manager-plugins xfce4-power-manager-data"
        )
        aptPurge("abiword gnumeric")
        aptPurge("popularity-contest")

        provisionKeys(gpg, ssh)
        provisionGit(gitUserName ?: whoami(), gitEmail, gpg?.let { gpgFingerprint(it.publicKey.plain()) })

        installFirefox()
        installGopass()
        configureGopass(publicGpgKey = gpg?.publicKey)
        installGopassJsonApi()
        downloadGopassBridge()

        installRedshift()
        configureRedshift()

        configureNoSwappiness()
        configureBash()
        installVirtualBoxGuestAdditions()
    } else if (onlyModules.contains(DesktopOnlyModule.FIREFOX.name.lowercase())) {
        installFirefox()
    }
}
