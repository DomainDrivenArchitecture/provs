package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall

fun Prov.installDeltaChat() = task {
    aptInstall("flatpak ca-certificates")
    cmd("sudo flatpak remote-add --if-not-exists flathub https://flathub.org/repo/flathub.flatpakrepo")
    cmd("sudo flatpak install -y --noninteractive flathub chat.delta.desktop")
}