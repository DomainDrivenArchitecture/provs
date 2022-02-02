package org.domaindrivenarchitecture.provs.server.domain.k3s

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.server.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.server.infrastructure.*
import org.domaindrivenarchitecture.provs.server.infrastructure.k3s.getK3sConfig

/**
 * Installs a k3s server.
 * If docker is true, then docker will be installed (may conflict if docker is already existing) and k3s will be installed with docker option.
 * If tlsHost is specified, then tls (if configured) also applies to the specified host.
 */
fun Prov.provisionK3s(configFileName: ConfigFileName?) = task {
    val k3sConfig: K3sConfig = getK3sConfig(configFileName!!)

    provisionNetwork(loopbackIpv4 = k3sConfig.loopback.ipv4, loopbackIpv6 = k3sConfig.loopback.ipv6!!)
    if (k3sConfig.reprovision && testConfigExists()) {
        deprovisionK3sInfra()
    }
    provisionK3sInfra(tlsName = k3sConfig.fqdn, nodeIpv4 = k3sConfig.node.ipv4, nodeIpv6 = k3sConfig.node.ipv6,
        loopbackIpv4 = k3sConfig.loopback.ipv4, loopbackIpv6 = k3sConfig.loopback.ipv6)
    provisionK3sCertManager(k3sConfig.letsencryptEndpoint)
    provisionK3sApple(k3sConfig.fqdn, k3sConfig.letsencryptEndpoint)
}
