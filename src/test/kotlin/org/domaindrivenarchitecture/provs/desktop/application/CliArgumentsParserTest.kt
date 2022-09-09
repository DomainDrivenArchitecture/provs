package org.domaindrivenarchitecture.provs.desktop.application

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class CliArgumentsParserTest {

    @Test
    fun parse_cliCommand_with_module_and_local_target() {
        val cli = CliArgumentsParser("test").parseCommand(args = arrayOf("basic", "local"))

        assertTrue(cli.isValid())
        assertEquals(null, cli.configFile)
        assertEquals(true, cli.target.isValidLocalhost())
    }

    @Test
    fun parse_cliCommand_with_onlyModule_teams_and_local_target() {
        val cli = CliArgumentsParser("test").parseCommand(args = arrayOf("ide", "local", "-o", "teams"))

        assertTrue(cli.isValid())
        assertEquals(true, cli.target.isValidLocalhost())
        assertEquals(true, cli.onlyModules?.contains("teams"))
    }
}