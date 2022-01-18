package org.domaindrivenarchitecture.provs.core.processors

import org.domaindrivenarchitecture.provs.framework.core.platforms.SHELL
import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerUbuntuHostProcessor
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.test.testDockerWithSudo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

val DEFAULT_START_MODE_TEST_CONTAINER = ContainerStartMode.USE_RUNNING_ELSE_CREATE

class ContainerUbuntuHostProcessorTest {

    @Test
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