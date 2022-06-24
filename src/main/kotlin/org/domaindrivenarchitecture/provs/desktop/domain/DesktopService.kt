package org.domaindrivenarchitecture.provs.desktop.domain

import org.domaindrivenarchitecture.provs.desktop.infrastructure.*
import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.git.provisionGit
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstallFromPpa
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptPurge
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.KeyPair
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base.gpgFingerprint
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.provisionKeys
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.base.currentUserCanSudo
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.base.whoami


fun provisionDesktop(prov: Prov, cmd: DesktopCliCommand) {
    val submodules = cmd.submodules
    // retrieve config
    val conf = if (cmd.configFile != null) getConfig(cmd.configFile.fileName) else DesktopConfig()

    if (submodules == null) {
        prov.provisionDesktop(cmd.type, conf.ssh?.keyPair(), conf.gpg?.keyPair(), conf.gitUserName, conf.gitEmail)
    } else {
        prov.provisionDesktopSubmodules(submodules)
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
fun Prov.provisionDesktop(
    desktopType: DesktopType = DesktopType.BASIC,
    ssh: KeyPair? = null,
    gpg: KeyPair? = null,
    gitUserName: String? = null,
    gitEmail: String? = null,
) = task {

    DesktopType.valueOf(desktopType.name) // throws exception when desktopType.name is unknown

    if (!currentUserCanSudo()) {
        throw Exception("Current user ${whoami()} cannot execute sudo without entering a password! This is necessary to execute provisionDesktop")
    }

    aptInstall(KEY_MANAGEMENT)
    aptInstall(VERSION_MANAGEMENT)
    aptInstall(NETWORK_TOOLS)
    aptInstall(SCREEN_TOOLS)

    provisionKeys(gpg, ssh)
    provisionGit(gitUserName ?: whoami(), gitEmail, gpg?.let { gpgFingerprint(it.publicKey.plain()) })

    installVirtualBoxGuestAdditions()

    aptPurge(
        "remove-power-management xfce4-power-manager " +
                "xfce4-power-manager-plugins xfce4-power-manager-data"
    )
    aptPurge("abiword gnumeric")
    aptPurge("popularity-contest")

    configureNoSwappiness()

    configureBash()

    if (desktopType == DesktopType.OFFICE || desktopType == DesktopType.IDE) {
        aptInstall(KEY_MANAGEMENT_GUI)
        aptInstall(BASH_UTILS)
        aptInstall(OS_ANALYSIS)
        aptInstall(ZIP_UTILS)
        aptInstall(PASSWORD_TOOLS)

        aptInstall(BROWSER)
        aptInstall(EMAIL_CLIENT)
        aptInstall(MESSENGER)
        aptInstall(OFFICE_SUITE)
        aptInstall(CLIP_TOOLS)

        installZimWiki()
        installGopass()
        aptInstallFromPpa("nextcloud-devs", "client", "nextcloud-client")

        optional {
            aptInstall(DRAWING_TOOLS)
        }

        aptInstall(SPELLCHECKING_DE)

        installRedshift()
        configureRedshift()
    }

    if (desktopType == DesktopType.IDE) {

        aptInstall(JAVA)

        aptInstall(OPEN_VPM)
        aptInstall(OPENCONNECT)
        aptInstall(VPNC)

        installDocker()

        // IDEs
        installVSC("python", "clojure")
        aptInstall(CLOJURE_TOOLS)
        installShadowCljs()

        installIntelliJ()

        installDevOps()

        provisionPython()
    }
    ProvResult(true)
}

private fun Prov.provisionDesktopSubmodules(
    submodules: List<String>,
) = task {

    if (submodules.contains(DesktopSubmodule.TEAMS.name.lowercase())) {
        installMsTeams()
    }
}