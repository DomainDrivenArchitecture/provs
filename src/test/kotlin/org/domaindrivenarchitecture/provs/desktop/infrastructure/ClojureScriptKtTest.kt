package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class ClojureScriptKtTest {

    @Test
    fun installShadowCljs() {
        // given
        val prov = defaultTestContainer(ContainerStartMode.CREATE_NEW_KILL_EXISTING)

        // when
        val res = prov.installShadowCljs()
        val res2 = prov.installShadowCljs()  // check if it can be run twice successfully

        // then
        assertTrue(res.success)
        assertTrue(res2.success)
    }
}