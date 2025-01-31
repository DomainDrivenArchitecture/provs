package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.assertTrue

internal class ClojureScriptKtTest {

    @ContainerTest
    fun installShadowCljs() {
        // given
        val prov = defaultTestContainer()

        //Todo:
        // To be discussed, should init container, but not available for prov.installShadowCljs() !!
        // Howto work in addition prov.a() + prov.b()?
        prov.installNpmByNvm()

        // when
        // check if it can be run twice successfully
        val res01 = prov.installShadowCljs()
        val res02 = prov.installShadowCljs()

        // then
        assertTrue(res01.success)
        assertTrue(res02.success)
    }
}