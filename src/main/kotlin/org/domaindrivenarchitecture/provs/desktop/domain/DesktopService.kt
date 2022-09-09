package org.domaindrivenarchitecture.provs.desktop.domain

import org.domaindrivenarchitecture.provs.desktop.infrastructure.*
import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.git.provisionGit
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptPurge
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.KeyPair
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base.gpgFingerprint
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.provisionKeys
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.base.currentUserCanSudo
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.base.whoami

internal fun provisionDesktopCmd(prov: Prov, cmd: DesktopCliCommand) {

    // retrieve config
    val conf = if (cmd.configFile != null) getConfig(cmd.configFile.fileName) else DesktopConfig()

    prov.provisionDesktop(cmd.type, conf.ssh?.keyPair(), conf.gpg?.keyPair(), conf.gitUserName, conf.gitEmail, cmd.onlyModules)
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
    ssh: KeyPair? = null,
    gpg: KeyPair? = null,
    gitUserName: String? = null,
    gitEmail: String? = null,
    onlyModules: List<String>?
) = task {
    validatePrecondition()
    provisionBasicDesktop(gpg, ssh, gitUserName, gitEmail, onlyModules)

    if (desktopType == DesktopType.OFFICE) {
        provisionOfficeDesktop(onlyModules)
        verifyOfficeSetup()
    }
    if (desktopType == DesktopType.IDE) {
        provisionOfficeDesktop(onlyModules)
        provisionIdeDesktop(onlyModules)
        verifyIdeSetup()
    }
    ProvResult(true)
}

fun Prov.validatePrecondition() {
    if (!currentUserCanSudo()) {
        throw Exception("Current user ${whoami()} cannot execute sudo without entering a password! This is necessary to execute provisionDesktop")
    }
}

fun Prov.provisionIdeDesktop(onlyModules: List<String>?) {
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

@Suppress("unused")
fun Prov.provisionMSDesktop(onlyModules: List<String>?) {
    if (onlyModules == null) {
        installMsTeams()
    } else if (onlyModules.contains(DesktopOnlyModule.TEAMS.name.lowercase())) {
        installMsTeams()
    }
}

fun Prov.provisionOfficeDesktop(onlyModules: List<String>?) {
    if (onlyModules == null) {
        aptInstall(ZIP_UTILS)
        aptInstall(BROWSER)
        aptInstall(EMAIL_CLIENT)
        installDeltaChat()
        aptInstall(OFFICE_SUITE)
        installZimWiki()
        installNextcloudClient()

        optional {
            aptInstall(DRAWING_TOOLS)
        }

        aptInstall(SPELLCHECKING_DE)
    } else if (onlyModules.contains(DesktopOnlyModule.VERIFY.name.lowercase())) {
        verifyOfficeSetup()
    } else if (onlyModules.contains(DesktopOnlyModule.FIREFOX.name.lowercase())) {
        installFirefox()
    }
}

fun Prov.provisionBasicDesktop(
    gpg: KeyPair?,
    ssh: KeyPair?,
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
        installRedshift()
        configureRedshift()
        configureNoSwappiness()
        configureBash()
        installVirtualBoxGuestAdditions()
    } else if (onlyModules.contains(DesktopOnlyModule.FIREFOX.name.lowercase())) {
        installFirefox()
    }
}
