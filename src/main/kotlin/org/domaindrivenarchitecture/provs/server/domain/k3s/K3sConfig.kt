package org.domaindrivenarchitecture.provs.server.domain.k3s

import kotlinx.serialization.Serializable
import org.domaindrivenarchitecture.provs.configuration.domain.Fqdn

@Serializable
data class K3sConfig(
    val fqdn: Fqdn,
    val node: Node,
    val loopback: Loopback = Loopback(ipv4 = "192.168.5.1", ipv6 = "fc00::5:1"),
    val certmanager: Certmanager? = null,
    val echo: Echo? = null,
    val reprovision: Reprovision = false,
    val monthlyReboot: Boolean = true,
) {
    fun isDualStack(): Boolean {
      return node.ipv6 != null && loopback.ipv6 != null
    }
}