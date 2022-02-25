package org.domaindrivenarchitecture.provs.framework.extensions.workplace

import org.domaindrivenarchitecture.provs.desktop.domain.DesktopType
import org.domaindrivenarchitecture.provs.desktop.domain.provisionWorkplace
import org.domaindrivenarchitecture.provs.desktop.domain.provisionWorkplaceSubmodules
import org.domaindrivenarchitecture.provs.desktop.infrastructure.getConfig
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.fileExists
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertTrue

internal class ProvisionWorkplaceKtTest {

    @ExtensiveContainerTest
    fun provisionWorkplace() {
        // given
        val a = defaultTestContainer()

        // when
        // in order to test WorkplaceType.OFFICE: fix installing libreoffice for a fresh container as it hangs the first time but succeeds 2nd time
        val res = a.provisionWorkplace(
            DesktopType.BASIC,
            gitUserName = "testuser",
            gitEmail = "testuser@test.org",
        )

        // then
        assertTrue(res.success)
    }


    @ExtensiveContainerTest
    fun provisionWorkplaceFromConfigFile() {
        // given
        val a = defaultTestContainer()

        // when
        // in order to test WorkplaceType.OFFICE: fix installing libreoffice for a fresh container as it hangs the first time but succeeds 2nd time
        val config = getConfig("src/test/resources/WorkplaceConfigExample.json")
        val res = a.provisionWorkplace(
            DesktopType.BASIC,
            config.ssh?.keyPair(),
            config.gpg?.keyPair(),
            config.gitUserName,
            config.gitEmail,
        )

        // then
        assertTrue(res.success)
    }


    @ExtensiveContainerTest
    fun provision_submodule_provsbinaries() {
        // given
        val prov = defaultTestContainer()

        // when
        val res = prov.provisionWorkplaceSubmodules(
            listOf("provsbinaries")
        )

        // then
        assertTrue(res.success)
        assertTrue(defaultTestContainer().fileExists(" /usr/local/bin/provs-server.jar", sudo = true))
        assertTrue(defaultTestContainer().fileExists(" /usr/local/bin/provs-desktop.jar", sudo = true))
    }

}


