package org.domaindrivenarchitecture.provs.desktop.domain

import org.domaindrivenarchitecture.provs.desktop.infrastructure.getConfig
import org.domaindrivenarchitecture.provs.framework.core.ProgressType
import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.docker.provideContainer
import org.domaindrivenarchitecture.provs.framework.core.local
import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerUbuntuHostProcessor
import org.domaindrivenarchitecture.provs.framework.core.remote
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.deleteFile
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class DesktopServiceKtTest {

    @ExtensiveContainerTest
    fun provisionLocalDesktop_fails_if_user_cannot_sudo_without_password() {
        // given
        val containerName = "prov-test-sudo-no-pw"
        local().provideContainer(containerName, "ubuntu_plus_user")
        val prov = Prov.newInstance(
            ContainerUbuntuHostProcessor(
                containerName,
                startMode = ContainerStartMode.USE_RUNNING_ELSE_CREATE,
                sudo = true,
                dockerImage = "ubuntu_plus_user"
            ),
            progressType = ProgressType.NONE
        )
        prov.deleteFile("/etc/sudoers.d/testuser", sudo = true)   // remove no password required

        // when
        Assertions.assertThrows(Exception::class.java) {
            prov.provisionDesktop(
                DesktopType.BASIC,
                gitUserName = "testuser",
                gitEmail = "testuser@test.org",
                onlyModules = null
            )
        }
    }

    @ExtensiveContainerTest
    fun provisionDesktop() {
        // given
        val prov = defaultTestContainer()

        // when
        // in order to test DesktopType.OFFICE: fix installing libreoffice for a fresh container as it hangs the first time but succeeds 2nd time
        val res = prov.provisionDesktop(
            DesktopType.BASIC,
            gitUserName = "testuser",
            gitEmail = "testuser@test.org",
            onlyModules = null
        )

        // then
        assertTrue(res.success)
    }

    @Test
    @Disabled
    // Run this test manually after having updated the ip and user in the test and commented out the @Disabled tag.
    // Does not run in a container, as DesktopType IDE includes several packages which need X-Windows.
    // Notes:
    // * to run this test, it must be possible to connect from the local to the remote machine by ssh with key authentication
    // * this test takes about 10 minutes
    fun provisionIDEDesktop() {
        // given
        val ip = "192.168.56.143"
        val user = "root"
        val prov = remote(ip, user)

        // when
        val res = prov.provisionDesktop(
            DesktopType.IDE,
            gitUserName = "testuser",
            gitEmail = "testuser@test.org",
            onlyModules = null
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
        val res = prov.provisionDesktop(
            DesktopType.BASIC,
            config.ssh?.keyPair(),
            config.gpg?.keyPair(),
            config.gitUserName,
            config.gitEmail,
            onlyModules = null
        )

        // then
        assertTrue(res.success)
    }
}


