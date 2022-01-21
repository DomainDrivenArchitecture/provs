package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFileFromResource
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.fileExists

val loopbackFile = "/etc/netplan/99-loopback.yaml"
val resourcePath = "org/domaindrivenarchitecture/provs/infrastructure/network/"

fun Prov.testNetworkExists(): Boolean {
    return fileExists(loopbackFile)
}

fun Prov.provisionNetwork() = task {
    if(!testNetworkExists()) {
        createFileFromResource(
            loopbackFile,
            "99-loopback.yaml.template",
            resourcePath,
            "644",
            sudo = true
        )
        cmd("netplan apply", sudo = true)
    } else {
        ProvResult(true)
    }
}