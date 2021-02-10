package io.provs.processors

import io.provs.platforms.SHELL
import io.provs.testconfig.tags.CONTAINERTEST
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS.LINUX


internal class ContainerUbuntuHostProcessorTest {

    @Test
    @EnabledOnOs(LINUX)
    @Tag(CONTAINERTEST)
    fun test() {
        if (System.getProperty("os.name") == "Linux") {
            val processor = ContainerUbuntuHostProcessor("UbuntuHostContainerExecution", "ubuntu", ContainerStartMode.CREATE_NEW_KILL_EXISTING)
            processor.installSudo()
            processor.x(SHELL, "-c", "'cd /home && mkdir blabla'")
            processor.exitAndRm()
        }
    }
}