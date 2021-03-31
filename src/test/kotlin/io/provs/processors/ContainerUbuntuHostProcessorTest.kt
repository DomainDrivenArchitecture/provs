package io.provs.processors

import io.provs.platforms.SHELL
import io.provs.test.DEFAULT_START_MODE_TEST_CONTAINER
import io.provs.test.tags.ContainerTest
import io.provs.test.testDockerWithSudo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS.LINUX


class ContainerUbuntuHostProcessorTest {

    @Test
    @EnabledOnOs(LINUX)
    @ContainerTest
    fun test() {
        val processor =
            ContainerUbuntuHostProcessor("provs_ubuntuhost_test", "ubuntu", DEFAULT_START_MODE_TEST_CONTAINER, sudo = testDockerWithSudo)
        processor.x(SHELL, "-c", "'cd /home && mkdir blabla'")
    }
}