package org.domaindrivenarchitecture.provs.framework.extensions.workplace

import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.configuration.domain.TargetCliCommand
import org.domaindrivenarchitecture.provs.desktop.application.DesktopCliCommand
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.desktop.domain.WorkplaceType
import org.domaindrivenarchitecture.provs.desktop.domain.provisionWorkplace
import org.domaindrivenarchitecture.provs.desktop.infrastructure.getConfig
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class ProvisionWorkplaceKtTest {

    val cmd = DesktopCliCommand(
        ConfigFileName("bla"),
        listOf(),
        TargetCliCommand(null, null, null, false, null, false)
    )

    @Test
    @ContainerTest
    fun provisionWorkplace() {
        // given
        val a = defaultTestContainer()

        // when
        // in order to test WorkplaceType.OFFICE: fix installing libreoffice for a fresh container as it hangs the first time but succeeds 2nd time
        val res = a.provisionWorkplace(
            WorkplaceType.MINIMAL,
            gitUserName = "testuser",
            gitEmail = "testuser@test.org",
            cmd = cmd
        )

        // then
        assertTrue(res.success)
    }


    @Test
    @ContainerTest
    fun provisionWorkplaceFromConfigFile() {
        // given
        val a = defaultTestContainer()

        // when
        // in order to test WorkplaceType.OFFICE: fix installing libreoffice for a fresh container as it hangs the first time but succeeds 2nd time
        val config = getConfig("src/test/resources/WorkplaceConfigExample.json")
        val res = a.provisionWorkplace(
            config.type,
            config.ssh?.keyPair(),
            config.gpg?.keyPair(),
            config.gitUserName,
            config.gitEmail,
            cmd,
        )

        // then
        assertTrue(res.success)
    }
}


