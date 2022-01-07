package org.domaindrivenarchitecture.provs.workplace.infrastructure

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.ubuntu.install.base.aptInstall
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class VSCodeKtTest {

    @Test
    fun provisionAdditionalTools() {
        // given
        defaultTestContainer().aptInstall("curl unzip")

        // when
        val res = defaultTestContainer().provisionAdditionalTools()

        // then
        assertTrue(res.success)
    }
}