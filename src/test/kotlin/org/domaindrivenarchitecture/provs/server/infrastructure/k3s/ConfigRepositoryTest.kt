package org.domaindrivenarchitecture.provs.server.infrastructure.k3s

import com.charleskorn.kaml.InvalidPropertyValueException
import com.charleskorn.kaml.UnknownPropertyException
import org.domaindrivenarchitecture.provs.server.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.server.domain.k3s.*
import org.domaindrivenarchitecture.provs.server.infrastructure.CertManagerEndPoint
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
        assertEquals(K3sConfig(
            "statistics.test.meissa-gmbh.de",
            Node("159.69.176.151", "2a01:4f8:c010:672f::1"),
            Loopback("192.168.5.1", "fc00::5:1"),
            true,
            CertManagerEndPoint.PROD), config)
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