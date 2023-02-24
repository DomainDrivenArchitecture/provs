package org.domaindrivenarchitecture.provs.configuration.application

import org.domaindrivenarchitecture.provs.configuration.domain.TargetCliCommand
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test


private fun parseTarget(
    args: Array<String>
): TargetCliCommand {
    val parser = CliTargetParser("provs")

    parser.parse(args)

    return TargetCliCommand(parser.target, parser.passwordInteractive)
}

internal class CliTargetParserTest {

    @Test
    fun parse_localhost() {
        val cliCommand = parseTarget(args = arrayOf("local"))
        assertTrue(cliCommand.isValidLocalhost())
        assertFalse(cliCommand.isValidRemote())
        assertTrue(cliCommand.isValid())
    }

    @Test
    fun parse_remote_with_given_pasword() {
        val cliCommand = parseTarget(args = arrayOf("user:mypassword@1.2.3.4"))

        assertFalse(cliCommand.isValidLocalhost())
        assertEquals("1.2.3.4", cliCommand.remoteTarget()?.host)
        assertEquals("user", cliCommand.remoteTarget()?.user)
        assertEquals("mypassword", cliCommand.remoteTarget()?.password?.plain())
        assertTrue(cliCommand.isValid())
    }

    @Test
    fun parse_remote_with_ssh_key() {
        val cliCommand = parseTarget(args = arrayOf("user@1.2.3.4"))

        assertFalse(cliCommand.isValidLocalhost())
        assertEquals("1.2.3.4", cliCommand.remoteTarget()?.host)
        assertEquals("user", cliCommand.remoteTarget()?.user)
        assertTrue(cliCommand.isValid())
    }

    @Test
    @Disabled // enable to enter manually the password when prompted
    fun parse_remote_with_password_prompt() {
        val cliCommand = parseTarget(args = arrayOf("user@1.2.3.4", "-p"))

        assertEquals("1.2.3.4", cliCommand.remoteTarget()?.host)
        assertEquals("user", cliCommand.remoteTarget()?.user)
        assertTrue(cliCommand.isValid())
    }
}