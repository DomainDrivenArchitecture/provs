package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.checkFile
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest

import org.junit.jupiter.api.Assertions.*

class GraalVMKtTest {

    @ExtensiveContainerTest
    fun installGraalVM() {
        // given
        val prov = defaultTestContainer()

        // when
        val res = prov.task {
            installGraalVM()
            installGraalVM()   // test repeatability
        }

        // then
        assertTrue(res.success)
        assertTrue(GRAAL_VM_VERSION == prov.graalVMVersion())
        assertTrue(prov.checkFile("/usr/local/bin/native-image"))
    }
}