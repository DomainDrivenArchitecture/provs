package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFileFromResource
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.isPackageInstalled

val pathSshConfig = "/etc/ssh/ssh_config"
val pathSshdConfig = "/etc/ssh/sshd_config"
val pathSshdHardeningConfig = "/etc/ssh/sshd_config.d/sshd_hardening.conf"
val packageNameSshServer = "openssh-server"
val resourcePathSsh = "org/domaindrivenarchitecture/provs/server/infrastructure/ssh/"

fun Prov.isSshdConfigExisting(): Boolean {
    return checkFile(pathSshdConfig)
}

fun Prov.isSshConfigExisting(): Boolean {
    return checkFile(pathSshConfig)
}

fun Prov.isSshdHardeningConfigExisting(): Boolean {
    return checkFile(pathSshdHardeningConfig)
}

fun Prov.configureSsh() = task {
    if(isSshdConfigExisting() && isSshConfigExisting() && isSshdHardeningConfigExisting() && isPackageInstalled(packageNameSshServer)) {
        createFileFromResource(
            pathSshConfig,
            "ssh_config",
            resourcePathSsh,
            "644",
            true)
        createFileFromResource(
            pathSshdConfig,
            "sshd_config",
            resourcePathSsh,
            "644",
            true)
        createFileFromResource(
            pathSshdHardeningConfig,
            "sshd_hardening.conf",
            resourcePathSsh,
            "644",
            true)
        cmd("service ssh restart", sudo = true)
    } else {
        ProvResult(false)
    }
}