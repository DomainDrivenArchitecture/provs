package org.domaindrivenarchitecture.provs.workplace.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall

fun Prov.installDocker() = def {
    aptInstall("containerd docker.io")
    if (!chk("getent group docker")) {
        cmd("sudo groupadd docker")
    }
    cmd("sudo gpasswd -a \$USER docker")
}