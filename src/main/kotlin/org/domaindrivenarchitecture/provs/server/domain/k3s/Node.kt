package org.domaindrivenarchitecture.provs.server.domain.k3s

import kotlinx.serialization.Serializable
import org.domaindrivenarchitecture.provs.configuration.domain.Ipv4
import org.domaindrivenarchitecture.provs.configuration.domain.Ipv6

@Serializable
data class Node(
    val ipv4: Ipv4,
    val ipv6: Ipv6? = null)
