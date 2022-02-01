package org.domaindrivenarchitecture.provs.server.infrastructure.k3s

import kotlinx.serialization.Serializable

@Serializable
data class K3sSerial(
    val fqdn: String,
    val nodeIpv4: String,
    val nodeIpv6: String? = null,
    val loopbackIpv4: String? = null,
    val loopbackIpv6: String? = null,
    val reprovision: Boolean? = null
) {

}