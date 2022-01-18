package org.domaindrivenarchitecture.provs.framework.core.processors

import org.domaindrivenarchitecture.provs.framework.core.newline
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS

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
