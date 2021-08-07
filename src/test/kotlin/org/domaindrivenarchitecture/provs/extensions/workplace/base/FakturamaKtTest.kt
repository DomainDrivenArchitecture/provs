package org.domaindrivenarchitecture.provs.extensions.workplace.base

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class FakturamaKtTest {

    @Test
    fun installFakturama() {
        // given
        val a = defaultTestContainer()
        // when
        val res = a.def { installFakturama() }
        // then
        assertTrue(res.success)
    }
}