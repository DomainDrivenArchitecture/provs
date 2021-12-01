package org.domaindrivenarchitecture.provs.core.cli

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class CliCommandTest {

    @Test
    fun parse_localhost_with_default() {
        val parseCli = parseCli(args = emptyArray())

        assertFalse(parseCli.isValidLocalhost())
        assertFalse(parseCli.isValidRemote())
        assertFalse(parseCli.isValid())
    }

    @Test
    fun parse_localhost() {
        val parseCli = parseCli(args = arrayOf("-l"))
        assertTrue(parseCli.isValidLocalhost())
        assertFalse(parseCli.isValidRemote())
        assertTrue(parseCli.isValid())
    }

    @Test
    fun parse_remote_with_missing_passwordoption() {
        val parseCli = parseCli(args = arrayOf("-r", "1.2.3.4", "-u", "user"))

        assertFalse(parseCli.isValidLocalhost())
        assertEquals("1.2.3.4", parseCli.remoteHost)
        assertEquals("user", parseCli.userName)
        assertFalse(parseCli.isValidRemote())
        assertFalse(parseCli.isValid())
    }

    @Test
    fun parse_remote_with_remote_key() {
        val parseCli = parseCli(args = arrayOf("-r", "1.2.3.4", "-u", "user", "-k"))

        assertFalse(parseCli.isValidLocalhost())
        assertEquals("1.2.3.4", parseCli.remoteHost)
        assertEquals("user", parseCli.userName)
        assertTrue(parseCli.isValid())
    }

    @Test
    fun parse_remote_with_remote_password_prompt() {
        val parseCli = parseCli(args = arrayOf("-r", "1.2.3.4", "-u", "user", "-i"))

        assertEquals("1.2.3.4", parseCli.remoteHost)
        assertEquals("user", parseCli.userName)
        assertTrue(parseCli.isValid())
    }

    @Test
    fun parse_remote_with_remote_password_gopass_path() {
        val parseCli = parseCli(args = arrayOf("-r", "1.2.3.4", "-u", "user", "-p", "gopass/path"))

        assertEquals("1.2.3.4", parseCli.remoteHost)
        assertEquals("user", parseCli.userName)
        assertEquals("gopass/path", parseCli.sshWithGopassPath)
        assertTrue(parseCli.isValid())
    }
}