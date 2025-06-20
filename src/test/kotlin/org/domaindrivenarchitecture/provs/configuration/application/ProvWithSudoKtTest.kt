package org.domaindrivenarchitecture.provs.configuration.application

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.domaindrivenarchitecture.provs.framework.core.*
import org.domaindrivenarchitecture.provs.framework.core.cli.getPasswordToConfigureSudoWithoutPassword
import org.domaindrivenarchitecture.provs.framework.core.docker.provideContainer
import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerUbuntuHostProcessor
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.deleteFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.infrastructure.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.infrastructure.currentUserCanSudoWithoutPassword
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.infrastructure.makeCurrentUserSudoerWithoutPasswordRequired
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue


class ProvWithSudoKtTest {


    @ExtensiveContainerTest
    fun test_ensureSudoWithoutPassword_local_Prov() {

        mockkStatic(::getPasswordToConfigureSudoWithoutPassword)
        every { getPasswordToConfigureSudoWithoutPassword() } returns Secret("testuser")

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
        prov.deleteFile("/etc/sudoers.d/testuser", sudo = true)   // remove no password required config

        // when
        val canSudo1 = prov.currentUserCanSudoWithoutPassword()
        prov.ensureSudoWithoutPassword(null)
        val canSudo2 = prov.currentUserCanSudoWithoutPassword()

        // then
        assertFalse(canSudo1)
        assertTrue(canSudo2)

        // cleanup
        unmockkAll()
    }

    @ExtensiveContainerTest
    fun test_ensureSudoWithoutPassword_remote_Prov() {

        // given
        val containerName = "prov-test-sudo-no-pw-ssh"
        val password = Secret("testuser")

        val prov = Prov.newInstance(
            ContainerUbuntuHostProcessor(
                containerName,
                startMode = ContainerStartMode.USE_RUNNING_ELSE_CREATE,
                sudo = true,
                dockerImage = "ubuntu_plus_user",
                options = "--expose=22"
            ),
            progressType = ProgressType.NONE
        )
        prov.makeCurrentUserSudoerWithoutPasswordRequired(password)
        prov.task {
            aptInstall("openssh-server")
            cmd("sudo service ssh start")
            deleteFile("/etc/sudoers.d/testuser", sudo = true)  // remove no password required config
        }
        val ip = local().cmd("sudo docker inspect -f \"{{ .NetworkSettings.IPAddress }}\" $containerName").out?.trim()
            ?: throw IllegalStateException("Ip not found")
        val remoteProvBySsh = remote(ip, "testuser", password)

        // when
        val canSudo1 = remoteProvBySsh.currentUserCanSudoWithoutPassword()
        prov.ensureSudoWithoutPassword(password)
        val canSudo2 = prov.currentUserCanSudoWithoutPassword()

        // then
        assertFalse(canSudo1)
        assertTrue(canSudo2)
    }
}