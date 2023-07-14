package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFileFromResourceTemplate
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkFile
import org.domaindrivenarchitecture.provs.server.domain.k3s.K3sConfig

val loopbackFile = "/etc/netplan/99-loopback.yaml"
val resourcePathNetwork = "org/domaindrivenarchitecture/provs/server/infrastructure/network/"

fun Prov.testNetworkExists(): Boolean {
    return checkFile(loopbackFile)
}

fun Prov.provisionNetwork(k3sConfig: K3sConfig) = task {
    if(!testNetworkExists()) {
        if(k3sConfig.isDualStack()) {
            require(k3sConfig.loopback.ipv6 != null)
            createFileFromResourceTemplate(
                loopbackFile,
                "99-loopback.dual.template.yaml",
                resourcePathNetwork,
                mapOf("loopback_ipv4" to k3sConfig.loopback.ipv4, "loopback_ipv6" to k3sConfig.loopback.ipv6),
                "644",
                sudo = true
            )
        } else {
            createFileFromResourceTemplate(
                loopbackFile,
                "99-loopback.ipv4.template.yaml",
                resourcePathNetwork,
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