package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertTrue

internal class PythonKtTest {

    @ExtensiveContainerTest
    fun installPython() {
        // when
        val res = defaultTestContainer().installPython()

        // then
        assertTrue(res.success)
    }
}