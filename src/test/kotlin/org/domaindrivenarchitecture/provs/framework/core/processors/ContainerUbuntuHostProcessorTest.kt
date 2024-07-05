package org.domaindrivenarchitecture.provs.framework.core.processors

import org.domaindrivenarchitecture.provs.framework.core.*
import org.domaindrivenarchitecture.provs.framework.core.platforms.SHELL
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.base.makeCurrentUserSudoerWithoutPasswordRequired
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.domaindrivenarchitecture.provs.test.testDockerWithSudo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

val DEFAULT_START_MODE_TEST_CONTAINER = ContainerStartMode.USE_RUNNING_ELSE_CREATE

class ContainerUbuntuHostProcessorTest {

    @ContainerTest
    fun test_execution() {
        // given
        val processor =
            ContainerUbuntuHostProcessor("provs_ubuntuhost_test", "ubuntu", DEFAULT_START_MODE_TEST_CONTAINER, sudo = testDockerWithSudo)

        // when
        val res = processor.exec(SHELL, "-c", "echo -n abc")

        // then
        assertEquals(0, res.exitCode)
        assertEquals("abc", res.out)
    }


    @ExtensiveContainerTest
    fun test_reopeing_ssh_session_succeeds() {

        // given
        val containerName = "prov-test-ssh-with-container"
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
        prov.task {
            makeCurrentUserSudoerWithoutPasswordRequired(password)
            aptInstall("openssh-server")
            cmd("sudo service ssh start")
        }

        val ipOfContainer = local().cmd("sudo docker inspect -f \"{{ .NetworkSettings.IPAddress }}\" $containerName").out?.trim()
            ?: throw IllegalStateException("Ip not found")
        val remoteProvBySsh = remote(ipOfContainer, "testuser", password)

        // when
        val firstSessionResult = remoteProvBySsh.cmd("echo 1")  // connect (to container) by ssh via ip
        val secondSessionResult = remoteProvBySsh.cmd("echo 2") // second connect after first connection has been closed

        // then
        assertTrue(firstSessionResult.success)
        assertTrue(secondSessionResult.success)
    }

}