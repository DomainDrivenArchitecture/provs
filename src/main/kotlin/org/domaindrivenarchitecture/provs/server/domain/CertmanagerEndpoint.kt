package org.domaindrivenarchitecture.provs.server.domain

enum class CertmanagerEndpoint {
    staging, prod;

    fun endpointUri(): String {
        return if (this == staging)
            "https://acme-staging-v02.api.letsencrypt.org/directory"
        else
            "https://acme-v02.api.letsencrypt.org/directory"
    }
}