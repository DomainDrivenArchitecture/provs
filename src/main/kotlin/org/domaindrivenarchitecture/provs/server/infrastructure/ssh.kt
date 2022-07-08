package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFileFromResource
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.isPackageInstalled

val pathSshdConfig = "/etc/ssh/sshd_config"
val packageNameSshServer = "openssh-server"
val resourcePathSsh = "org/domaindrivenarchitecture/provs/server/infrastructure/ssh/"

fun Prov.isSshdConfigExisting(): Boolean {
    return checkFile(pathSshdConfig)
}

fun Prov.configureSshd() = task {
    if(isSshdConfigExisting() && isPackageInstalled(packageNameSshServer)) {
        createFileFromResource(
            pathSshdConfig,
            "sshd_config",
            resourcePathSsh,
            "644",
            true)
        cmd("service ssh restart", sudo = true)
    } else {
        ProvResult(false)
    }
}