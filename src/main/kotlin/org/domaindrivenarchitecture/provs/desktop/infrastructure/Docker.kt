package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.infrastructure.aptInstall

fun Prov.installDocker() = task {
    aptInstall("containerd docker.io")
    if (!chk("getent group docker")) {
        cmd("sudo groupadd docker")
    }
    cmd("sudo gpasswd -a \$USER docker")
}