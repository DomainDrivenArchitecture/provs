package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.writeToFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.addTextToFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstallFromPpa
import java.io.File


fun Prov.installFirefox(

) = task {

    cmd("snap remove firefox", sudo = true)
    aptInstall("software-properties-common")
    cmd("sudo add-apt-repository -y ppa:mozillateam/ppa")

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
}