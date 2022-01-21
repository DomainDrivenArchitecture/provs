package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class VSCodeKtTest {

    @Test
    @ContainerTest
    fun provisionAdditionalTools() {
        // given
        defaultTestContainer().aptInstall("curl unzip")

        // when
        val res = defaultTestContainer().provisionAdditionalTools()

        // then
        assertTrue(res.success)
    }
}