package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertTrue

class GoKtTest {

    @ExtensiveContainerTest
    fun installGo() {
        // given
        val prov = defaultTestContainer()

        // when
        val res = prov.task {
            installGo()
            installGo()  // check repeatability
        }

        // then
        assertTrue(res.success)
    }
}