package org.domaindrivenarchitecture.provs.desktop.domain

import org.domaindrivenarchitecture.provs.desktop.infrastructure.getConfig
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows

internal class DesktopServiceKtTest {

    @ContainerTest
    fun provisionDesktop_fails_for_unknown_DesktopType() {
        // given
        val prov = defaultTestContainer()

        // when
        val exception = assertThrows<RuntimeException> {
            prov.provisionDesktopImpl(
                DesktopType("iamunkown"),
                gitUserName = "testuser",
                gitEmail = "testuser@test.org",
                submodules = null
            )
        }

        // then
        assertEquals("No DesktopType found for value: iamunkown", exception.message)
    }

    @ExtensiveContainerTest
    fun provisionDesktop() {
        // given
        val prov = defaultTestContainer()

        // when
        // in order to test DesktopType.OFFICE: fix installing libreoffice for a fresh container as it hangs the first time but succeeds 2nd time
        val res = prov.provisionDesktopImpl(
            DesktopType.BASIC,
            gitUserName = "testuser",
            gitEmail = "testuser@test.org",
            submodules = null
        )

        // then
        assertTrue(res.success)
    }

    @ExtensiveContainerTest
    fun provisionIDEDesktop() {
        // given
        val prov = defaultTestContainer()

        // when
        // in order to test DesktopType.OFFICE: fix installing libreoffice for a fresh container as it hangs the first time but succeeds 2nd time
        val res = prov.provisionDesktopImpl(
            DesktopType.IDE,
            gitUserName = "testuser",
            gitEmail = "testuser@test.org",
            submodules = null
        )

        // then
        assertTrue(res.success)
    }


    @ExtensiveContainerTest
    fun provisionDesktopFromConfigFile() {
        // given
        val prov = defaultTestContainer()

        // when
        // in order to test DesktopType.OFFICE: fix installing libreoffice for a fresh container as it hangs the first time but succeeds 2nd time
        val config = getConfig("src/test/resources/desktop-config-example.json")
        val res = prov.provisionDesktopImpl(
            DesktopType.BASIC,
            config.ssh?.keyPair(),
            config.gpg?.keyPair(),
            config.gitUserName,
            config.gitEmail,
            submodules = null
        )

        // then
        assertTrue(res.success)
    }
}


