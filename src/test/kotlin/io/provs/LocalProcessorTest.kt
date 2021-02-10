package io.provs

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS


internal class LocalProcessorTest {

    @Test
    @EnabledOnOs(OS.LINUX)
    fun cmd_with_printf_on_Linux() {
        // given
        val prov = Prov.newInstance()
        val text = "abc123!§\\\$%%&/\"\\äöü'"

        // when
        val res = prov.cmd("printf '${text.replace("%", "%%").escapeSingleQuoteForShell()}'")

        // then
        assert(res.success)
        assert(res.out == text)//"abc123!§\\$%&/\"\\äöü'")
    }


    @Test
    @EnabledOnOs(OS.LINUX)
    fun cmd_with_nested_shell_and_printf_on_Linux() {
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
    @EnabledOnOs(OS.WINDOWS)
    fun cmd_with_echo_on_Windows() {
        // given
        val prov = Prov.newInstance()
        val text = "abc123!\"#"

        // when
        val res = prov.cmd("echo $text")

        // then
        assert(res.success)
        assertEquals( text + "\r\n", res.out)
    }


    @Test
    @EnabledOnOs(OS.LINUX)
    fun cmdNoLog_linux() {
        // given
        val prov = Prov.newInstance()
        val text = "abc123!#"
        val osSpecificText = if (OS.WINDOWS.isCurrentOs) text else "'$text'"


        // when
        val res = prov.cmdNoLog("echo $osSpecificText")

        // then
        assert(res.success)
        assertEquals( text + System.lineSeparator(), res.out)
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