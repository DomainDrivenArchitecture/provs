package org.domaindrivenarchitecture.provs.server.domain.k3s

data class K3sConfig(
    val fqdn: Fqdn,
    val nodeIpv4: Ipv4,
    val nodeIpv6: Ipv6?,
    val loopbackIpv4: Ipv4? = Ipv4("192.168.5.1"),
    val loopbackIpv6: Ipv6? = Ipv6("fc00::5:1"),
    val reprovision: Reprovision? = Reprovision(false)
) {

}