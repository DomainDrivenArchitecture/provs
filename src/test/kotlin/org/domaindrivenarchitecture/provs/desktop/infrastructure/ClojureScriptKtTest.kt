package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

internal class ClojureScriptKtTest {

    @ContainerTest
    fun installShadowCljs() {
        // given
        val prov = defaultTestContainer()
        prov.task {
            aptInstall("curl")
            installNpmByNvm()
        }

        // when
        val res = prov.task {
            installShadowCljs()
            installShadowCljs()  // check repeatability
        }

        // then
        assertTrue(res.success)
    }

    @ExtensiveContainerTest  // extensive test, as it kills the existing container and creates a new one
    fun installShadowCljs_fails_if_nvm_missing() {
        // given
        val prov = defaultTestContainer(ContainerStartMode.CREATE_NEW_KILL_EXISTING)

        // when
        val res = prov.installShadowCljs()

        // then
        assertFalse(res.success)
    }
}