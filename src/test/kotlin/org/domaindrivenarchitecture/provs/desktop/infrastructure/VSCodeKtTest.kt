package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class VSCodeKtTest {

    @Test
    @ExtensiveContainerTest
    fun provisionAdditionalTools() {
        // given
        defaultTestContainer().aptInstall("curl unzip")

        // when
        val res = defaultTestContainer().provisionAdditionalTools()

        // then
        assertTrue(res.success)
    }
}