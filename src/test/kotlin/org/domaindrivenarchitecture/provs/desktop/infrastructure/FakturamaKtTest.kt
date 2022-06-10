package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertTrue

internal class FakturamaKtTest {

    @ExtensiveContainerTest
    fun installFakturama() {
        // given
        val a = defaultTestContainer()
        // when
        val res = a.task { installFakturama() }
        // then
        assertTrue(res.success)
    }
}