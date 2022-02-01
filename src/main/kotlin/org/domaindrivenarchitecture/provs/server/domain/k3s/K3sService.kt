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

    provisionNetwork(loopbackIpv4 = k3sConfig.loopbackIpv4!!.ip, loopbackIpv6 = k3sConfig.loopbackIpv6!!.ip)
    if (k3sConfig.reprovision!!.it && testConfigExists()) {
        deprovisionK3sInfra()
    }
    provisionK3sInfra(tlsName = k3sConfig.fqdn.it, nodeIpv4 = k3sConfig.nodeIpv4.ip, nodeIpv6 = k3sConfig.nodeIpv6?.ip,
        loopbackIpv4 = k3sConfig.loopbackIpv4!!.ip, loopbackIpv6 = k3sConfig.loopbackIpv6.ip)
    provisionK3sCertManager(CertManagerEndPoint.STAGING)
    provisionK3sApple(k3sConfig.fqdn.it, CertManagerEndPoint.STAGING)
}
