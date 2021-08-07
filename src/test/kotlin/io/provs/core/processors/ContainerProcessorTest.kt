package io.provs.core.processors

import io.provs.core.newline
import io.provs.test.defaultTestContainer
import io.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS

@EnabledOnOs(OS.LINUX)
internal class ContainerProcessorTest {

    @Test
    @ContainerTest
    fun cmd_works_with_echo() {

        // given
        val prov = defaultTestContainer()
        val text = "abc123!§$%&/#äöü"

        // when
        val res = prov.cmd("echo '${text}'")

        // then
        assert(res.success)
        assertEquals(text + newline(), res.out)
    }


    @Test
    @ContainerTest
    fun cmdNoLog_works_with_echo() {
        // given
        val prov = defaultTestContainer()
        val text = "abc123!§$%&/#äöü"

        // when
        val res = prov.cmdNoLog("echo '${text}'")

        // then
        assert(res.success)
        assertEquals(text + newline(), res.out)

        // todo add check that cmd was not logged
    }
}
