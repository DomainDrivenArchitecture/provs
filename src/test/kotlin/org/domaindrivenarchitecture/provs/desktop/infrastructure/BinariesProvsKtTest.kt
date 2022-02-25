package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.fileExists
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.assertTrue

internal class BinariesProvsKtTest {

    @ContainerTest
    fun installBinariesProvs() {
        // given
        val prov = defaultTestContainer()

        // when
        val res = prov.installBinariesProvs()

        // then
        assertTrue(res.success)
        assertTrue(defaultTestContainer().fileExists(" /usr/local/bin/provs-server.jar", sudo = true))
        assertTrue(defaultTestContainer().fileExists(" /usr/local/bin/provs-desktop.jar", sudo = true))
    }
}