package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkFile
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest

import org.junit.jupiter.api.Assertions.*

class KubeconformKtTest {

    @ContainerTest
    fun installKubeconform() {
        // given
        val prov = defaultTestContainer()

        // when
        val res = prov.installKubeconform()

        // then
        assertTrue(res.success)
        assertTrue(prov.checkFile("bin/kubeconform"))
    }
}