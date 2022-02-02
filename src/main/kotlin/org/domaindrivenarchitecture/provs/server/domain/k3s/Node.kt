package org.domaindrivenarchitecture.provs.server.domain.k3s

import kotlinx.serialization.Serializable

@Serializable
data class Node(
    val ipv4: Ipv4,
    val ipv6: Ipv6?)
