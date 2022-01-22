package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFileFromResourceTemplate
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.fileExists

val loopbackFile = "/etc/netplan/99-loopback.yaml"
val resourcePath = "org/domaindrivenarchitecture/provs/infrastructure/network/"

fun Prov.testNetworkExists(): Boolean {
    return fileExists(loopbackFile)
}

fun Prov.provisionNetwork() = task {
    if(!testNetworkExists()) {
        createFileFromResourceTemplate(
            loopbackFile,
            "99-loopback.yaml.template",
            resourcePath,
            mapOf("ip" to "192.168.5.1/32"),
            "644",
            sudo = true
        )
        cmd("netplan apply", sudo = true)
    } else {
        ProvResult(true)
    }
}