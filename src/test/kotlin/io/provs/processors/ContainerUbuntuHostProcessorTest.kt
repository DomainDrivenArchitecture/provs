package io.provs.processors

import io.provs.test.DEFAULT_START_MODE_TEST_CONTAINER
import io.provs.platforms.SHELL
import io.provs.test.testDockerWithSudo
import io.provs.test.tags.CONTAINERTEST
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS.LINUX


class ContainerUbuntuHostProcessorTest {

    @Test
    @EnabledOnOs(LINUX)
    @Tag(CONTAINERTEST)
    fun test() {
        val processor =
            ContainerUbuntuHostProcessor("provs_ubuntuhost_test", "ubuntu", DEFAULT_START_MODE_TEST_CONTAINER, sudo = testDockerWithSudo)
        processor.x(SHELL, "-c", "'cd /home && mkdir blabla'")
    }
}