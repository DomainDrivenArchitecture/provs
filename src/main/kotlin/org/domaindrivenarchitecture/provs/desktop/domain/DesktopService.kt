package org.domaindrivenarchitecture.provs.desktop.domain

import org.domaindrivenarchitecture.provs.desktop.application.DesktopCliCommand
import org.domaindrivenarchitecture.provs.desktop.infrastructure.*
import org.domaindrivenarchitecture.provs.desktop.infrastructure.getConfig
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
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.base.downloadFromURL

fun provisionDesktop(prov: Prov, cmd: DesktopCliCommand) {
    // retrieve config
    val conf = getConfig(cmd.configFile.fileName)
    with(conf) {
        prov.provisionWorkplace(type, ssh?.keyPair(), gpg?.keyPair(), gitUserName, gitEmail, cmd)
    }
}

/**
 * Provisions software and configurations for a personal workplace.
 * Offers the possibility to choose between different types.
 * Type OFFICE installs office-related software like Thunderbird, LibreOffice, and much more.
 * Type IDE provides additional software for a development environment, such as Visual Studio Code, IntelliJ, etc.
 *
 * Prerequisites: user must be able to sudo without entering the password
 */
fun Prov.provisionWorkplace(
    workplaceType: WorkplaceType = WorkplaceType.MINIMAL,
    ssh: KeyPair? = null,
    gpg: KeyPair? = null,
    gitUserName: String? = null,
    gitEmail: String? = null,
    cmd: DesktopCliCommand
) = requireAll {

    if (!currentUserCanSudo()) {
        throw Exception("Current user ${whoami()} cannot execute sudo without entering a password! This is necessary to execute provisionWorkplace")
    }

    if (cmd.haScope()) {
        if (cmd.scopes.contains(Scope.PROVS)) {
            downloadFromURL(
                url="https://gitlab.com/domaindrivenarchitecture/provs/-/jobs/2046149473/artifacts/file/build/libs/provs-server.jar",
                filename = "provs-server.jar",
                path = "/usr/local/bin/",
                sha256sum = "cec1c8762ce310694bacef587ad26b3bb7b8482a8548330ccaf9c9d3eb052409",
                sudo = true
            )
            downloadFromURL(
                url="https://gitlab.com/domaindrivenarchitecture/provs/-/jobs/2046149473/artifacts/file/build/libs/provs-desktop.jar",
                filename = "provs-desktop.jar",
                path = "/usr/local/bin/",
                sha256sum = "61bad1380809325aca95bfbcb7cf27928ee070ed886c5de7e300797961d1fa58",
                sudo = true
            )
            cmd("chmod 755 /usr/local/bin/provs-server.jar" , sudo = true)
            cmd("chmod 755  /usr/local/bin/provs-desktop.jar", sudo = true)
        }
        ProvResult(true)
    } else {
        aptInstall(KEY_MANAGEMENT)
        aptInstall(VERSION_MANAGEMENT)
        aptInstall(NETWORK_TOOLS)

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

        if (workplaceType == WorkplaceType.OFFICE || workplaceType == WorkplaceType.IDE) {
            aptInstall(KEY_MANAGEMENT_GUI)
            aptInstall(BASH_UTILS)
            aptInstall(OS_ANALYSIS)
            aptInstall(ZIP_UTILS)
            aptInstall(PASSWORD_TOOLS)

            aptInstall(BROWSER)
            aptInstall(EMAIL_CLIENT)
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

        if (workplaceType == WorkplaceType.IDE) {

            aptInstall(JAVA_JDK)

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

            installPython()
        }
        ProvResult(true)
    }
}
