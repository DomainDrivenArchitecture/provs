package org.domaindrivenarchitecture.provs.framework.core.processors

import org.domaindrivenarchitecture.provs.framework.core.newline
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

internal class ContainerProcessorTest {

    @ContainerTest
    fun cmd_works_with_echo() {

        // given
        val prov = defaultTestContainer()
        val text = "abc123!§$%&/#äöü"

        // when
        val res = prov.cmd("echo '${text}'")

        // then
        assertTrue(res.success)
        assertEquals(text + newline(), res.out)
    }


    @ContainerTest
    fun cmdNoLog_works_with_echo() {
        // given
        val prov = defaultTestContainer()
        val text = "abc123!§$%&/#äöü"

        // when
        val res = prov.cmdNoLog("echo '${text}'")

        // then
        assertTrue(res.success)
        assertEquals(text + newline(), res.out)

        // todo add check that cmd was not logged
    }
}
