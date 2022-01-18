package org.domaindrivenarchitecture.provs.framework.extensions.workplace.base

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.workplace.infrastructure.installFakturama
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class FakturamaKtTest {

    @Test
    @ContainerTest
    fun installFakturama() {
        // given
        val a = defaultTestContainer()
        // when
        val res = a.def { installFakturama() }
        // then
        assertTrue(res.success)
    }
}