package org.domaindrivenarchitecture.provs.server.infrastructure

import kotlinx.serialization.SerializationException
import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.server.domain.CertmanagerEndpoint
import org.domaindrivenarchitecture.provs.server.domain.k3s.Certmanager
import org.domaindrivenarchitecture.provs.server.domain.k3s.K3sConfig
import org.domaindrivenarchitecture.provs.server.domain.k3s.Loopback
import org.domaindrivenarchitecture.provs.server.domain.k3s.Node
import org.domaindrivenarchitecture.provs.server.infrastructure.k3s.getK3sConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.FileNotFoundException

internal class K3sConfigRepositoryTest {

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
                    letsencryptEndpoint = CertmanagerEndpoint.prod
                ),
                apple = true,
                reprovision = true
            ), config
        )
    }

    @Test
    fun getConfig_fails_due_to_missing_property() {
        val exception = assertThrows<SerializationException> {
            getK3sConfig(ConfigFileName("src/test/resources/InvalidWorkplaceConfig.yaml"))
        }
        assertEquals("Fields [fqdn, node] are required for type with serial name 'org.domaindrivenarchitecture.provs.server.domain.k3s.K3sConfig', but they were missing", exception.message)
    }

    @Test
    fun getConfig_fails_due_to_missing_file() {
        val exception = assertThrows<FileNotFoundException> {
            getK3sConfig(ConfigFileName("src/test/resources/Idonotexist.yaml"))
        }
        assertEquals("src/test/resources/Idonotexist.yaml (No such file or directory)", exception.message)
    }
}