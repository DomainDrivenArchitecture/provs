package org.domaindrivenarchitecture.provs.server.infrastructure.k3s

import com.charleskorn.kaml.UnknownPropertyException
import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.server.domain.k3s.*
import org.domaindrivenarchitecture.provs.server.domain.CertmanagerEndpoint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.FileNotFoundException

internal class ConfigRepositoryTest {

    @Test
    fun getConfig_successful() {
        // when
        val config = getK3sConfig(ConfigFileName("src/test/resources/k3sServerConfig.yaml"))

        // then
        assertEquals(
            K3sConfig(
                fqdn = "statistics.test.meissa-gmbh.de",
                node = Node("162.55.164.138", "2a01:4f8:c010:672f::1"),
                loopback = Loopback("192.168.5.1", "fc00::5:1"),
                certmanager = Certmanager(
                    email = "admin@meissa-gmbh.de",
                    letsencryptEndpoint = CertmanagerEndpoint.PROD
                ),
                apple = true,
                reprovision = true
            ), config
        )
    }

    @Test
    fun getConfig_fails_due_to_invalidProperty() {
        assertThrows<UnknownPropertyException> {
            getK3sConfig(ConfigFileName("src/test/resources/InvalidWorkplaceConfig.yaml"))
        }

    }

    @Test
    fun getConfig_fails_due_to_non_existing_file() {
        assertThrows<FileNotFoundException> {
            getK3sConfig(ConfigFileName("src/test/resources/Idonotexist.yaml"))
        }

    }
}