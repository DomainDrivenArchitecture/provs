package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.base.whoami

fun Prov.installVirtualBoxGuestAdditions() = task {
    // if running in a VirtualBox vm
    if (!chk("lspci | grep VirtualBox")) {
        return@task ProvResult(true, "Not running in a VirtualBox")
    }

    if (chk("VBoxService --version")) {
        return@task ProvResult(true, "VBoxService already installed")
    }

    // install guest additions
    cmd("sudo add-apt-repository multiverse")
    aptInstall("virtualbox-guest-x11") // virtualbox-guest-dkms")
    // and add user to group vboxsf e.g. to be able to use shared folders
    whoami()?.let { cmd("sudo usermod -G vboxsf -a " + it) }
        ?: ProvResult(true)
}
