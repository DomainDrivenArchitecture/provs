package io.provs.ubuntu.extensions.workplace.base

import io.provs.core.Prov
import io.provs.ubuntu.install.base.aptInstall

fun Prov.installDocker() = def {
    aptInstall("containerd docker.io")
    if (!chk("getent group docker")) {
        cmd("sudo groupadd docker")
    }
    cmd("sudo gpasswd -a \$USER docker")
}