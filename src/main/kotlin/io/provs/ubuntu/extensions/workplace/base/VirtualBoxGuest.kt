package io.provs.ubuntu.extensions.workplace.base

import io.provs.Prov
import io.provs.ProvResult
import io.provs.remote
import io.provs.ubuntu.install.base.aptInstall
import io.provs.ubuntu.secret.secretSources.GopassSecretSource
import io.provs.ubuntu.secret.secretSources.PlainSecretSource
import io.provs.ubuntu.secret.secretSources.PromptSecretSource
import io.provs.ubuntu.user.base.whoami

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
