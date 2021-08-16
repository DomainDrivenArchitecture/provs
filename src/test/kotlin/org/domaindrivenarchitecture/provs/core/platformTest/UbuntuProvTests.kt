package org.domaindrivenarchitecture.provs.core.platformTest

import org.domaindrivenarchitecture.provs.core.ProgressType
import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.docker.dockerImageExists
import org.domaindrivenarchitecture.provs.core.docker.dockerProvideImage
import org.domaindrivenarchitecture.provs.core.docker.dockerimages.DockerImage
import org.domaindrivenarchitecture.provs.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.core.processors.ContainerUbuntuHostProcessor
import org.domaindrivenarchitecture.provs.test.defaultTestContainerName
import org.domaindrivenarchitecture.provs.test.tags.NonCi
import org.domaindrivenarchitecture.provs.test.testDockerWithSudo
import org.domaindrivenarchitecture.provs.test.testLocal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS

internal class UbuntuProvTests {

    private fun Prov.ping(url: String) = def {
        xec("ping", "-c", "4", url)
    }

    private fun Prov.outerPing() = def {
        ping("gitlab.com")
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    fun that_ping_works() {
        // when
        val res = testLocal().outerPing()

        // then
        assert(res.success)
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    fun that_cmd_works() {
        // given
        val a = testLocal()

        // when
        val res1 = a.cmd("pwd")
        val dir = res1.out?.trim()
        val res2 = a.cmd("echo abc", dir)

        // then
        assert(res1.success)
        assert(res2.success)
        assert(res2.out?.trim() == "abc")
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    @NonCi
    fun that_cmd_works_with_sudo() {
        // given
        val a = testLocal()

        // when
        val res1 = a.cmd("echo abc", "/root", sudo = true)

        // then
        assert(res1.success)
        assert(res1.out?.trim() == "abc")
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    fun that_nested_shells_work() {
        // given
        val a = testLocal()

        // when
        val res1 = a.cmd("pwd")
        val dir = res1.out?.trim()
        val res2 = a.cmd("echo abc", dir)

        // then
        assert(res1.success)
        assert(res2.success)
        assert(res2.out?.trim() == "abc")
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    fun that_xec_works() {
        // given
        val a = testLocal()

        // when
        val res1 = a.xec("/usr/bin/printf", "hi")
        val res2 = a.xec("/bin/ping", "-c", "2", "gitlab.com")
        val res3 = a.xec("/bin/bash", "-c", "echo echoed")

        // then
        assert(res1.success)
        assert(res1.out?.trim() == "hi")
        assert(res2.success)
        assert(res3.success)
        assert(res3.out?.trim() == "echoed")
    }

    @Test
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
        assertEquals("sudo: no tty present and no askpass program specified\n", result.err)
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
FROM ubuntu:18.04

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get -y install sudo
RUN useradd -m $userName && echo "$userName:$userName" | chpasswd && adduser $userName sudo

USER $userName
CMD /bin/bash
WORKDIR /home/$userName
"""
    }
}
