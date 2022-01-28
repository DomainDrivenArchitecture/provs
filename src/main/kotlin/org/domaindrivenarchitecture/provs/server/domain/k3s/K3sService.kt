package org.domaindrivenarchitecture.provs.server.domain.k3s

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.core.echoCommandForText
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.server.infrastructure.provisionK3sInfra
import org.domaindrivenarchitecture.provs.server.infrastructure.provisionNetwork


/**
 * Installs a k3s server.
 * If docker is true, then docker will be installed (may conflict if docker is already existing) and k3s will be installed with docker option.
 * If tlsHost is specified, then tls (if configured) also applies to the specified host.
 */
fun Prov.provisionK3s() = task {
    val loopbackIpv4 = "192.168.5.1"
    val loopbackIpv6 = "fc00::5:1"
    val nodeIpv4 = "162.55.164.138"
    val nodeIpv6 = "2a01:4f8:c010:622b::1"

    provisionNetwork(loopbackIpv4 = loopbackIpv4, loopbackIpv6 = loopbackIpv6)
    provisionK3sInfra(tlsName = "statistics.prod.meissa-gmbh.de", nodeIpv4 = nodeIpv4, nodeIpv6 = nodeIpv6,
        loopbackIpv4 = loopbackIpv4, loopbackIpv6 = loopbackIpv6, installApple = true)
}
