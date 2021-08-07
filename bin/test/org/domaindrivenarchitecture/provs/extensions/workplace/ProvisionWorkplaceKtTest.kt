package org.domaindrivenarchitecture.provs.extensions.workplace

import org.domaindrivenarchitecture.provs.core.Password
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.domaindrivenarchitecture.provs.test.defaultTestContainer

internal class ProvisionWorkplaceKtTest {

    @Test
    fun provisionWorkplace() {
        // given
        val a = defaultTestContainer()

        // when
        // in order to test WorkplaceType.OFFICE: fix installing libreoffice for a fresh container as it hangs the first time but succeeds 2nd time
        val res = a.provisionWorkplace(
            WorkplaceType.MINIMAL,
            gitUserName = "testuser",
            gitEmail = "testuser@test.org",
            userPassword = Password("testuser")
        )

        // then
        assertTrue(res.success)
    }


    @Test
    fun provisionWorkplaceFromConfigFile() {
        // given
        val a = defaultTestContainer()

        // when
        // in order to test WorkplaceType.OFFICE: fix installing libreoffice for a fresh container as it hangs the first time but succeeds 2nd time
        val config = readWorkplaceConfigFromFile("src/test/resources/WorkplaceConfigExample.json")
            ?: throw Exception("Could not read WorkplaceConfig")
        val res = a.provisionWorkplace(
            config.type,
            config.ssh?.keyPair(),
            config.gpg?.keyPair(),
            config.gitUserName,
            config.gitEmail,
        )

        // then
        assertTrue(res.success)
    }
}


