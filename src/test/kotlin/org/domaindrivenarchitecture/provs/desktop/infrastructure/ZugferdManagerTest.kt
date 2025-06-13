package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.createDir
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.*

internal class ZugferdManagerTest {
    @ExtensiveContainerTest
    fun test_installZugferdManager() {
        // given
        val prov = defaultTestContainer()

        // when
        val res = prov.task {
            createDir("/usr/share/desktop-directories/", sudo = true)
            installZugferdManager()
            installZugferdManager()   // check repeatability
        }

        // then
        assertTrue(res.success)
    }
}