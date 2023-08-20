package org.domaindrivenarchitecture.provs.desktop.domain

import io.mockk.*
import org.domaindrivenarchitecture.provs.desktop.infrastructure.installPpaFirefox
import org.domaindrivenarchitecture.provs.desktop.infrastructure.verifyIdeSetup
import org.domaindrivenarchitecture.provs.desktop.infrastructure.verifyOfficeSetup
import org.domaindrivenarchitecture.provs.framework.core.*
import org.domaindrivenarchitecture.provs.framework.core.docker.provideContainer
import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerUbuntuHostProcessor
import org.domaindrivenarchitecture.provs.framework.core.processors.DummyProcessor
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

    @Test
    fun provisionDesktop_with_onlyModules_firefox_installs_firefox() {
        // given
        val prov = Prov.newInstance(DummyProcessor())
        mockkStatic(Prov::installPpaFirefox)
        every { any<Prov>().installPpaFirefox() } returns ProvResult(true, cmd = "mocked")

        // when
        prov.provisionDesktop(DesktopType.IDE, onlyModules = listOf("firefox"))

        // then
        verify(exactly = 1) { any<Prov>().installPpaFirefox() }

        // cleanup
        unmockkAll()
    }

    @Test
    fun provisionDesktop_ide_with_onlyModules_verify_performs_verification() {
        // given
        val prov = Prov.newInstance(DummyProcessor())
        mockkStatic(Prov::verifyIdeSetup)
        mockkStatic(Prov::verifyOfficeSetup)
        mockkStatic(Prov::provisionBasicDesktop)
        every { any<Prov>().verifyIdeSetup() } returns ProvResult(true, cmd = "mocked")
        every { any<Prov>().verifyOfficeSetup() } returns ProvResult(true, cmd = "mocked")
        every { any<Prov>().provisionBasicDesktop(any(), any(), any(), any()) }

        // when
        prov.provisionDesktop(DesktopType.IDE, onlyModules = listOf("verify"))

        // then
        verify(exactly = 1) { any<Prov>().verifyIdeSetup() }
        verify(exactly = 0) { any<Prov>().verifyOfficeSetup() }
        verify(exactly = 0) { any<Prov>().provisionBasicDesktop(any(), any(), any(), any()) }

        // cleanup
        unmockkAll()
    }

    @Test
    fun provisionDesktop_office_with_onlyModules_verify_performs_verification() {
        // given
        val prov = Prov.newInstance(DummyProcessor())
        mockkStatic(Prov::verifyIdeSetup)
        mockkStatic(Prov::verifyOfficeSetup)
        mockkStatic(Prov::provisionBasicDesktop)
        every { any<Prov>().verifyIdeSetup() } returns ProvResult(true, cmd = "mocked")
        every { any<Prov>().verifyOfficeSetup() } returns ProvResult(true, cmd = "mocked")
        every { any<Prov>().provisionBasicDesktop(any(), any(), any(), any()) }

        // when
        prov.provisionDesktop(DesktopType.OFFICE, onlyModules = listOf("verify"))

        // then
        verify(exactly = 0) { any<Prov>().verifyIdeSetup() }
        verify(exactly = 1) { any<Prov>().verifyOfficeSetup() }
        verify(exactly = 0) { any<Prov>().provisionBasicDesktop(any(), any(), any(), any()) }

        // cleanup
        unmockkAll()
    }


    @ExtensiveContainerTest
    @Disabled("Takes very long, enable if you want to test a desktop setup")
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
}


