package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.domain.SecretSourceType
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.domain.SecretSupplier
import org.domaindrivenarchitecture.provs.server.domain.hetzner_csi.HetznerCSIConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class HetznerCSIRepositoryKtTest {

    @Test
    fun findHetznerCSIConfig_returns_config() {
        // when
        val config = findHetznerCSIConfig(ConfigFileName("src/test/resources/k3s-server-config-with-hetzner.yaml"))

        // then
        assertEquals(
            HetznerCSIConfig(
                hcloudApiToken = SecretSupplier(SecretSourceType.GOPASS, "path/to/apitoken"),
                encryptionPassphrase = SecretSupplier(SecretSourceType.GOPASS, "path/to/encryption"),
            ), config
        )
    }

    @Test
    fun findHetznerCSIConfig_returns_null_if_no_hetzner_data_available() {
        // when
        val config = findHetznerCSIConfig(ConfigFileName("src/test/resources/k3s-server-config.yaml"))

        // then
        assertEquals(null, config)
    }
}