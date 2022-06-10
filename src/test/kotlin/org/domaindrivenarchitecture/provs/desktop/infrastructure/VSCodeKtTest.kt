package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class VSCodeKtTest {

    @ExtensiveContainerTest
    fun provisionAdditionalTools() {
        // given
        defaultTestContainer().aptInstall("curl unzip")

        // when
        val res = defaultTestContainer().provisionAdditionalToolsForVSCode()

        // then
        assertTrue(res.success)
    }

    @Test
    @Disabled("Test currently not working, needs fix. VSC is installed by snapd which is not currently supported to run inside docker")
    fun installVSC() {
        // given
        val a = defaultTestContainer()
        a.aptInstall("xvfb libgbm-dev libasound2")

        // when
        val res = a.installVSC("python", "clojure")

        // then
        assertTrue(res.success)
    }
}