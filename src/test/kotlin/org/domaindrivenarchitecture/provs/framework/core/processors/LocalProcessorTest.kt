package org.domaindrivenarchitecture.provs.framework.core.processors

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.escapeAndEncloseByDoubleQuoteForShell
import org.domaindrivenarchitecture.provs.framework.core.escapeProcentForPrintf
import org.domaindrivenarchitecture.provs.framework.core.escapeSingleQuoteForShell
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.nio.file.Paths


internal class LocalProcessorTest {

    @Test
    fun cmd_with_printf() {
        // given
        val prov = Prov.newInstance()
        val text = "abc123!§\\\$%%&/\"\\äöü'"

        // when
        val res = prov.cmd("printf '${text.replace("%", "%%").escapeSingleQuoteForShell()}'")

        // then
        assertTrue(res.success)
        assertTrue(res.out == text)
    }

    @Test
    fun cmd_in_folder_where_program_was_started() {
        // given
        val prov = Prov.newInstance(LocalProcessor(false))

        // when
        val pwd = prov.cmd("pwd").outTrimmed

        // then
        assertEquals(Paths.get("").toAbsolutePath().toString(), pwd)
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
        assertTrue(res.success)
        assertEquals( text + System.lineSeparator(), res.out)

        // todo add check that cmd was not logged
    }


    @Test
    fun cmd_forUnknownCommand_resultWithError() {
        // given
        val prov = Prov.newInstance()

        // when
        val res = prov.cmd("iamanunknowncmd")

        // then
        assertFalse(res.success)
        assertTrue(res.out.isNullOrEmpty())
        assertFalse(res.err.isNullOrEmpty())
    }
}