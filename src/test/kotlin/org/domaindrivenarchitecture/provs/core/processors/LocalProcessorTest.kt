package org.domaindrivenarchitecture.provs.core.processors

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.escapeAndEncloseByDoubleQuoteForShell
import org.domaindrivenarchitecture.provs.framework.core.escapeProcentForPrintf
import org.domaindrivenarchitecture.provs.framework.core.escapeSingleQuoteForShell
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


internal class LocalProcessorTest {

    @Test
    fun cmd_with_printf() {
        // given
        val prov = Prov.newInstance()
        val text = "abc123!§\\\$%%&/\"\\äöü'"

        // when
        val res = prov.cmd("printf '${text.replace("%", "%%").escapeSingleQuoteForShell()}'")

        // then
        assert(res.success)
        assert(res.out == text)
    }


    @Test
    fun cmd_with_nested_shell_and_printf() {
        // given
        val prov = Prov.newInstance()
        val text = "abc123!§\\$%%&/\"\\äöü'"

        // when
        val res = prov.cmd("sh -c " + ("sh -c " + ("printf ${text.escapeProcentForPrintf().escapeAndEncloseByDoubleQuoteForShell()}").escapeAndEncloseByDoubleQuoteForShell()).escapeAndEncloseByDoubleQuoteForShell())

        // then
        assertTrue(res.success)
        assertEquals(text, res.out)
    }


    @Test
    fun cmdNoLog() {
        // given
        val prov = Prov.newInstance()
        val text = "abc123!#"
        val osSpecificText = "'$text'"

        // when
        val res = prov.cmdNoLog("echo $osSpecificText")

        // then
        assert(res.success)
        assertEquals( text + System.lineSeparator(), res.out)

        // todo add check that cmd was not logged
    }


    @Test
    fun cmd_forUnkownCommand_resultWithError() {
        // given
        val prov = Prov.newInstance()

        // when
        val res = prov.cmd("iamanunknowncmd")

        // then
        assert(!res.success)
        assert(res.out.isNullOrEmpty())
        assert(!res.err.isNullOrEmpty())
    }
}