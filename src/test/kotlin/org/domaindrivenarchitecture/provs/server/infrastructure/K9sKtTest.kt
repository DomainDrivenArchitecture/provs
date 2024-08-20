package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.assertTrue

class K9sKtTest {

    @ContainerTest
    fun installK9s() {
        // given
        val prov = defaultTestContainer()

        // when
        val res = prov.task {
            installK9s()
            installK9s()  // test repeatability
        }

        // then
        assertTrue(res.success)
    }
}