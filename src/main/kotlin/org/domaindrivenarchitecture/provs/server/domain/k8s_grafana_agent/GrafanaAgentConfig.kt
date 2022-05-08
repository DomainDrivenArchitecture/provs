package org.domaindrivenarchitecture.provs.server.domain.k8s_grafana_agent

import kotlinx.serialization.Serializable
import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.SecretSupplier

@Serializable
data class GrafanaAgentConfig(
    val user: String,
    val password: SecretSupplier,
    val cluster: String
) {
    fun resolveSecret(): GrafanaAgentConfigResolved = GrafanaAgentConfigResolved(this)
}

data class GrafanaAgentConfigResolved(val configUnresolved: GrafanaAgentConfig) {
    val user: String = configUnresolved.user
    val password: Secret = configUnresolved.password.secret()
    val cluster: String = configUnresolved.cluster
}

@Serializable
data class GrafanaAgentConfigHolder(
    val grafana: GrafanaAgentConfig
)
