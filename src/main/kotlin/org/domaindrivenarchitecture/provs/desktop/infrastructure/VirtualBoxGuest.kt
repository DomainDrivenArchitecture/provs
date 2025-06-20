package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.infrastructure.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.infrastructure.whoami

fun Prov.installVirtualBoxGuestAdditions() = taskWithResult {
    // if running in a VirtualBox vm
    if (!chk("lspci | grep VirtualBox")) {
        return@taskWithResult ProvResult(true, "Not running in a VirtualBox")
    }

    if (chk("VBoxService --version")) {
        return@taskWithResult ProvResult(true, "VBoxService already installed")
    }

    // install guest additions
    cmd("sudo add-apt-repository multiverse")
    aptInstall("virtualbox-guest-x11") // virtualbox-guest-dkms")
    // and add user to group vboxsf e.g. to be able to use shared folders
    whoami()?.let { cmd("sudo usermod -G vboxsf -a " + it) }
        ?: ProvResult(true)
}
