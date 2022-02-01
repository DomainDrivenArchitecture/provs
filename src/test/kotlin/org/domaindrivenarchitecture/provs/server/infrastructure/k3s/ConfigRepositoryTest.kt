package org.domaindrivenarchitecture.provs.server.infrastructure.k3s

import com.charleskorn.kaml.InvalidPropertyValueException
import com.charleskorn.kaml.UnknownPropertyException
import org.domaindrivenarchitecture.provs.server.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.server.domain.k3s.Fqdn
import org.domaindrivenarchitecture.provs.server.domain.k3s.Ipv4
import org.domaindrivenarchitecture.provs.server.domain.k3s.Ipv6
import org.domaindrivenarchitecture.provs.server.domain.k3s.Reprovision
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.FileNotFoundException

internal class ConfigRepositoryTest {

    @Test
    fun getConfig_successful() {
        // when
        val config = getK3sConfig(ConfigFileName("src/test/resources/myK3sServerConfig.yaml"))

        // then
        assertEquals(Ipv4("159.69.176.151"), config.nodeIpv4)
        assertEquals(Ipv6("2a01:4f8:c010:672f::1"), config.nodeIpv6)
        assertEquals(Ipv4("192.168.5.1"), config.loopbackIpv4)
        assertEquals(Ipv6("fc00::5:1"), config.loopbackIpv6)
        assertEquals(Fqdn("statistics.test.meissa-gmbh.de"), config.fqdn)
        assertEquals(Reprovision(true), config.reprovision)
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