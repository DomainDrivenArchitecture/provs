package org.domaindrivenarchitecture.provs.framework.core.platformTest

import org.domaindrivenarchitecture.provs.framework.core.ProgressType
import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.docker.dockerImageExists
import org.domaindrivenarchitecture.provs.framework.core.docker.dockerProvideImage
import org.domaindrivenarchitecture.provs.framework.core.docker.dockerimages.DockerImage
import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerUbuntuHostProcessor
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.test.tags.NonCi
import org.domaindrivenarchitecture.provs.test.testDockerWithSudo
import org.domaindrivenarchitecture.provs.test.testLocal
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class UbuntuProvTest {

    @Test
    fun that_cmd_works() {
        // given
        val a = testLocal()

        // when
        val res1 = a.cmd("pwd")
        val dir = res1.out?.trim()
        val res2 = a.cmd("echo abc", dir)

        // then
        assertTrue(res1.success)
        assertTrue(res2.success)
        assertTrue(res2.out?.trim() == "abc")
    }

    @Test
    @NonCi
    fun that_cmd_works_with_sudo() {
        // given
        val a = testLocal()

        // when
        val res1 = a.cmd("echo abc", "/root", sudo = true)

        // then
        assertTrue(res1.success)
        assertTrue(res1.out?.trim() == "abc")
    }

    @Test
    fun that_nested_shells_work() {
        // given
        val a = testLocal()

        // when
        val res1 = a.cmd("pwd")
        val dir = res1.out?.trim()
        val res2 = a.cmd("echo abc", dir)

        // then
        assertTrue(res1.success)
        assertTrue(res2.success)
        assertTrue(res2.out?.trim() == "abc")
    }

    @Test
    fun that_xec_works() {
        // given
        val a = testLocal()

        // when
        val res1 = a.exec("/usr/bin/printf", "hi")
        val res2 = a.exec("/bin/bash", "-c", "echo echoed")

        // then
        assertTrue(res1.success)
        assertTrue(res1.out?.trim() == "hi")
        assertTrue(res2.success)
        assertTrue(res2.out?.trim() == "echoed")
    }

    @ContainerTest
    @NonCi
    fun test_user_cannot_sudo_without_password() {
        // given
        val image = UbuntuUserNeedsPasswordForSudo()
        val prov = testLocal()
        if (!prov.dockerImageExists(image.imageName(), true)) {
            prov.dockerProvideImage(image, sudo = true)
        }

        val a = Prov.newInstance(
            ContainerUbuntuHostProcessor(
                "provs_test_without_password_for_sudo",
                startMode = ContainerStartMode.CREATE_NEW_KILL_EXISTING,
                sudo = testDockerWithSudo,
                dockerImage = image.imageName()
            ),
            progressType = ProgressType.NONE
        )

        // when
        val result = a.cmd("sudo echo bla")

        // then
        assertFalse(result.success)
        val expectedMsg = "a password is required"
        assertTrue(result.err?.contains(expectedMsg) ?: false, "Error: [$expectedMsg] is not found in [${result.err}]")
    }

}

/**
 * Provides a docker image based on ubuntu additionally with a non-root default user and sudo installed but user needs password to sudo
 */
class UbuntuUserNeedsPasswordForSudo(private val userName: String = "testuser") : DockerImage {

    override fun imageName(): String {
        return "ubuntu_user_needs_password_for_sudo"
    }

    override fun imageText(): String {
        return """
FROM ubuntu:24.04

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get -y install sudo
RUN useradd -m $userName && echo "$userName:$userName" | chpasswd && usermod -aG sudo $userName

USER $userName
CMD /bin/bash
WORKDIR /home/$userName
"""
    }
}
