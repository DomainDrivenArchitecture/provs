package org.domaindrivenarchitecture.provs.extensions.workplace.base

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.ProvResult
import org.domaindrivenarchitecture.provs.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.ubuntu.user.base.whoami

fun Prov.installVirtualBoxGuestAdditions() = def {
    // if running in a VirtualBox vm
    if (!chk("lspci | grep VirtualBox")) {
        return@def ProvResult(true, "Not running in a VirtualBox")
    }

    if (chk("VBoxService --version")) {
        return@def ProvResult(true, "VBoxService already installed")
    }

    // install guest additions
    cmd("sudo add-apt-repository multiverse")
    aptInstall("virtualbox-guest-x11") // virtualbox-guest-dkms")
    // and add user to group vboxsf e.g. to be able to use shared folders
    whoami()?.let { cmd("sudo usermod -G vboxsf -a " + it) }
        ?: ProvResult(true)
}
