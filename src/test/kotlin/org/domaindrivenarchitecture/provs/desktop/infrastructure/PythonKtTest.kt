package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertTrue

internal class PythonKtTest {

    @ExtensiveContainerTest
    fun test_provisionPython() {
        // when
        val res = defaultTestContainer().provisionPython()

        // then
        assertTrue(res.success)
    }
}