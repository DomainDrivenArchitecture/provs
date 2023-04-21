package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.addTextToFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import java.io.File


/**
 * Installs non-snap firefox, removing a firefox snap-installation if existing
 */
fun Prov.installFirefox() = task {

    // inspired by: https://www.omgubuntu.co.uk/2022/04/how-to-install-firefox-deb-apt-ubuntu-22-04

    task("remove snap firefox") {
        if (chk("snap list | grep firefox")) {
            cmd("snap remove firefox", sudo = true)
        }
    }

    aptInstall("software-properties-common")
    cmd("add-apt-repository -y ppa:mozillateam/ppa", sudo = true)

    // set prio in order to use ppa-firefox above snap
    addTextToFile(
        "\nPackage: *\n" +
                "Pin: release o=LP-PPA-mozillateam\n" +
                "Pin-Priority: 1001\n",
        File("/etc/apt/preferences.d/mozilla-firefox"),
        sudo = true
    )

    addTextToFile(
        """Unattended-Upgrade::Allowed-Origins:: "LP-PPA-mozillateam:${'$'}{distro_codename}";""",
        File("/etc/apt/preferences.d/mozilla-firefox"),
        sudo = true
    )

    aptInstall("firefox")
    cmd("apt upgrade -y firefox", sudo = true)
}
