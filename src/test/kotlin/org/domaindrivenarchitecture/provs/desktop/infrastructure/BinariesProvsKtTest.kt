package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkFile
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertTrue

internal class BinariesProvsKtTest {

    @ExtensiveContainerTest
    fun installBinariesProvs() {
        // given
        val prov = defaultTestContainer()

        // when
        val res = prov.installBinariesProvs()

        // then
        assertTrue(res.success)
        assertTrue(defaultTestContainer().checkFile(" /usr/local/bin/provs-server.jar", sudo = true))
        assertTrue(defaultTestContainer().checkFile(" /usr/local/bin/provs-desktop.jar", sudo = true))
    }
}