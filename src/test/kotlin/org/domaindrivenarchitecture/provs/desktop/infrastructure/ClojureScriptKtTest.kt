package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.assertTrue

internal class ClojureScriptKtTest {

    @ContainerTest
    fun installShadowCljs() {
        // given
        val prov = defaultTestContainer()

        prov.installNpmByNvm()

        // when
        val res = prov.installShadowCljs()
        val res2 = prov.installShadowCljs()  // check if it can be run twice successfully

        // then
        assertTrue(res.success)
        assertTrue(res2.success)
    }
}