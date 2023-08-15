package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstallFromPpa


/**
 * Installs ppa firefox (i.e. non-snap), removing snap-firefox if existing.
 */
fun Prov.installPpaFirefox() = taskWithResult {

    // inspired by: https://wiki.ubuntuusers.de/Firefox/Installation/PPA/

    val unattendeUpgradesForPpaFirefox = "/etc/apt/apt.conf.d/51unattended-upgrades-firefox"

    val preCondition = checkFile(unattendeUpgradesForPpaFirefox)
    if (preCondition) {
        return@taskWithResult ProvResult(true, out = "Firefox already installed with ppa")
    }

    cmd("sudo apt-get -qy remove firefox", sudo = true)
    optional("remove snap firefox") {
        cmd("snap remove firefox", sudo = true)
    }

    createFile("/etc/apt/preferences.d/mozillateam", mozillaTeamFileContent, sudo = true)

    aptInstallFromPpa("mozillateam", "ppa", "firefox")

    createFile(
        unattendeUpgradesForPpaFirefox,
        "Unattended-Upgrade::Allowed-Origins:: \"LP-PPA-mozillateam:\${distro_codename}\";\n",
        sudo = true
    )
}


private val mozillaTeamFileContent = """
    Package: * 
    Pin: release o=LP-PPA-mozillateam
    Pin-Priority: 100

    Package: firefox*
    Pin: release o=LP-PPA-mozillateam
    Pin-Priority: 1001

    Package: firefox*
    Pin: release o=Ubuntu
    Pin-Priority: -1
    
    Package: thunderbird*
    Pin: release o=LP-PPA-mozillateam
    Pin-Priority: 1001

    Package: thunderbird*
    Pin: release o=Ubuntu
    Pin-Priority: -1
""".trimIndent()