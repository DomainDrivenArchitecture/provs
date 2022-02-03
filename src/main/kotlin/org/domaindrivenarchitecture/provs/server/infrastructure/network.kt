package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFileFromResourceTemplate
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.fileExists
import org.domaindrivenarchitecture.provs.server.domain.k3s.K3sConfig

val loopbackFile = "/etc/netplan/99-loopback.yaml"
val resourcePath = "org/domaindrivenarchitecture/provs/infrastructure/network/"

fun Prov.testNetworkExists(): Boolean {
    return fileExists(loopbackFile)
}

fun Prov.provisionNetwork(k3sConfig: K3sConfig) = task {
    if(!testNetworkExists()) {
        if(k3sConfig.isDualStack()) {
            createFileFromResourceTemplate(
                loopbackFile,
                "99-loopback.dual.template.yaml",
                resourcePath,
                mapOf("loopback_ipv4" to k3sConfig.loopback.ipv4, "loopback_ipv6" to k3sConfig.loopback.ipv6!!),
                "644",
                sudo = true
            )
        } else {
            createFileFromResourceTemplate(
                loopbackFile,
                "99-loopback.ipv4.template.yaml",
                resourcePath,
                mapOf("loopback_ipv4" to k3sConfig.loopback.ipv4),
                "644",
                sudo = true
            )
        }
        cmd("netplan apply", sudo = true)
    } else {
        ProvResult(true)
    }
}