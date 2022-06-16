package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.SecretSourceType
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.SecretSupplier
import org.domaindrivenarchitecture.provs.server.domain.k8s_grafana_agent.GrafanaAgentConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class GrafanaAgentRepositoryKtTest {

    @Test
    fun findK8sGrafanaConfig_returns_config() {
        // when
        val config = findK8sGrafanaConfig(ConfigFileName("src/test/resources/k3s-server-config-with-grafana.yaml"))

        // then
        assertEquals(
            GrafanaAgentConfig(
                user = "654321",
                password = SecretSupplier(SecretSourceType.GOPASS, "path/to/key"),
                cluster = "myclustername",
                url = "https://prometheus-prod-01-eu-west-0.grafana.net/api/prom/push"
            ), config
        )
    }

    @Test
    fun findK8sGrafanaConfig_returns_null_if_no_grafan_data_available() {
        // when
        val config = findK8sGrafanaConfig(ConfigFileName("src/test/resources/k3s-server-config.yaml"))

        // then
        assertEquals(null, config)
    }
}