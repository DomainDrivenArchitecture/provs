package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class PythonKtTest {

    @Test
    @ContainerTest
    fun installPython() {
        // when
        val res = defaultTestContainer().installPython()

        // then
        assertTrue(res.success)
    }
}