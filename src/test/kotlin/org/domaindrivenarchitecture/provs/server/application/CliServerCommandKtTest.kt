package org.domaindrivenarchitecture.provs.server.application

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class CliServerCommandKtTest {

    @Test
    fun test_parseServerArguments_are_valid_for_k3s() {
        // when
        val cmd = parseServerArguments(args = arrayOf("-l", "-t", "k3s"))

        // then
        assertTrue(cmd.isValid())
        assertEquals(CliServerArgumentsParser.K3sType.K3S, cmd.type())
    }

    @Test
    fun test_parseServerArguments_are_invalid_without_target() {
        // when
        val cmd = parseServerArguments(args = arrayOf("-t", "k3s"))

        // then
        assertFalse(cmd.isValid())
        assertEquals(CliServerArgumentsParser.K3sType.K3S, cmd.type())
    }

    @Test
    fun test_parseServerArguments_has_default_type_k3s() {
        // when
        val cmd = parseServerArguments(args = arrayOf("-l"))

        // then
        assertTrue(cmd.isValid())
        assertEquals(CliServerArgumentsParser.K3sType.K3S, cmd.type())
    }

    @Test
    fun test_parseServerArguments_are_valid_for_k3d() {
        // when
        val cmd = parseServerArguments(args = arrayOf("-l", "-t", "k3d"))

        // then
        assertTrue(cmd.isValid())
        assertEquals(CliServerArgumentsParser.K3sType.K3D, cmd.type())
    }
}