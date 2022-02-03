package org.domaindrivenarchitecture.provs.server.domain.k3s

import kotlinx.serialization.Serializable
import org.domaindrivenarchitecture.provs.server.domain.CertmanagerEndpoint

@Serializable
data class Certmanager(
    val email: Email,
    val letsencryptEndpoint: CertmanagerEndpoint
)
