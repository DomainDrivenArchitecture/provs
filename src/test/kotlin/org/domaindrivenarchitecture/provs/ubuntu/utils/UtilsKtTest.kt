package org.domaindrivenarchitecture.provs.ubuntu.utils

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.echoCommandForText
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class UtilsKtTest {

    @ContainerTest
    @Test
    fun printToShell_escapes_successfully() {
        // given
        val a = Prov.defaultInstance()

        // when
        val testString = "test if newline \n and apostrophe's ' \" and special chars !§$%[]\\ äöüß \$variable are handled correctly"
        val res = a.cmd(echoCommandForText(testString)).out

        // then
        assertEquals(testString, res)
    }
}