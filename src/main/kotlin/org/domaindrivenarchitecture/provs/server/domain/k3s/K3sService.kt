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
    provisionNetwork(loopbackIpv4 = "192.168.5.1", loopbackIpv6 = "fc00::5:1")
    provisionK3sInfra(tlsName = "statistics.prod.meissa-gmbh.de", nodeIpv4 = "162.55.166.39", nodeIpv6 = "2a01:4f8:c010:622b::1",
        loopbackIpv4 = "192.168.5.1", loopbackIpv6 = "fc00::5:1")
}
