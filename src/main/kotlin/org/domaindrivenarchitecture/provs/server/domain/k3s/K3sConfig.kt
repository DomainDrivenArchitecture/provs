package org.domaindrivenarchitecture.provs.server.domain.k3s

import kotlinx.serialization.Serializable
import org.domaindrivenarchitecture.provs.server.infrastructure.CertManagerEndPoint

@Serializable
data class K3sConfig(
    val fqdn: Fqdn,
    val node: Node,
    val loopback: Loopback = Loopback(ipv4 = "192.168.5.1", ipv6 = "fc00::5:1"),
    val reprovision: Reprovision = false,
    val letsencryptEndpoint: CertManagerEndPoint = CertManagerEndPoint.STAGING
) {
}