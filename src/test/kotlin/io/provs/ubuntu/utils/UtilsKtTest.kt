package io.provs.ubuntu.utils

import io.provs.core.Prov
import io.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class UtilsKtTest {

    @ContainerTest
    @Test
    fun printToShell_escapes_successfully() {
        // given
        val a = Prov.defaultInstance()

        // when
        val testString = "test if newline \n and apostrophe's ' \" and special chars !§$%[]\\ äöüß are handled correctly"
        val res = a.cmd(printToShell(testString)).out

        // then
        assertEquals(testString, res)
    }
}