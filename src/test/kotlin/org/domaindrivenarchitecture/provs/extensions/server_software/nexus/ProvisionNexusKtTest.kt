package nexus

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.extensions.server_software.nexus.provisionNexusWithDocker
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class ProvisionNexusKtTest {

    @Test
    @Disabled("Find out how to run docker in docker")
    fun provisionNexusWithDocker() {
        // given
        val a = defaultTestContainer()

        // when
        val res = a.requireAll {
            provisionNexusWithDocker()
        }

        // then
        assertTrue(res.success)
    }
}