package io.provs

import io.provs.processors.ContainerStartMode
import io.provs.processors.ContainerUbuntuHostProcessor
import io.provs.testconfig.tags.CONTAINERTEST
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS

@EnabledOnOs(OS.LINUX)
internal class ContainerProcessorTest {

    @Test
    @Tag(CONTAINERTEST)
    fun cmd_works_with_echo() {
        // given
        val prov = Prov.newInstance(ContainerUbuntuHostProcessor("provs_test", startMode = ContainerStartMode.CREATE_NEW_KILL_EXISTING))
        val text = "abc123!§$%&/#äöü"

        // when
        val res = prov.cmd("echo '${text}'")

        // then
        assert(res.success)
        assertEquals(text + newline(), res.out)
    }


    @Test
    @Tag(CONTAINERTEST)
    fun cmdNoLog_works_with_echo() {
        // given
        val prov = Prov.newInstance(ContainerUbuntuHostProcessor("provs_test", startMode = ContainerStartMode.CREATE_NEW_KILL_EXISTING))
        val text = "abc123!§$%&/#äöü"

        // when
        val res = prov.cmdNoLog("echo '${text}'")

        // then
        assert(res.success)
        assertEquals(text + newline(), res.out)
    }
}