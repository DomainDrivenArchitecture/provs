package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDir
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.base.downloadFromURL


fun Prov.installFakturama() = task {
    createDir("/tmp", sudo = true)
    downloadFromURL( "https://files.fakturama.info/release/v2.1.1/Installer_Fakturama_linux_x64_2.1.1b.deb", "fakturama.deb", "/tmp")
    cmd("sudo dpkg -i fakturama.deb", "/tmp")

    createDir("/opt/fakturama", sudo = true)
    val filename = "Handbuch-Fakturama_2.1.1.pdf"
    downloadFromURL( "https://files.fakturama.info/release/v2.1.1/Handbuch-Fakturama_2.1.1.pdf", filename, "/tmp")
    cmd("sudo mv /tmp/$filename /opt/fakturama")
}
