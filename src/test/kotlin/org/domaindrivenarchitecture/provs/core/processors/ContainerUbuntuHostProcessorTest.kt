package org.domaindrivenarchitecture.provs.core.processors

import org.domaindrivenarchitecture.provs.core.platforms.SHELL
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.test.testDockerWithSudo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS.LINUX

val DEFAULT_START_MODE_TEST_CONTAINER = ContainerStartMode.USE_RUNNING_ELSE_CREATE

class ContainerUbuntuHostProcessorTest {

    @Test
    @EnabledOnOs(LINUX)
    @ContainerTest
    fun test_execution() {
        // given
        val processor =
            ContainerUbuntuHostProcessor("provs_ubuntuhost_test", "ubuntu", DEFAULT_START_MODE_TEST_CONTAINER, sudo = testDockerWithSudo)

        // when
        val res = processor.x(SHELL, "-c", "echo -n abc")

        // then
        assertEquals(0, res.exitCode)
        assertEquals("abc", res.out)
    }
}