package org.domaindrivenarchitecture.provs.server.application

import org.domaindrivenarchitecture.provs.server.application.CliK3sArgumentsParser
import org.domaindrivenarchitecture.provs.server.application.parseServerArguments
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class CliK3sCommandKtTest {

    @Test
    fun test_parseServerArguments_are_valid_for_k3s() {
        // when
        val cmd = parseServerArguments(args = arrayOf("-l", "-t", "k3s"))

        // then
        assertTrue(cmd.isValid())
        assertEquals(CliK3sArgumentsParser.K3sType.K3S, cmd.type())
    }

    @Test
    fun test_parseServerArguments_are_invalid_without_target() {
        // when
        val cmd = parseServerArguments(args = arrayOf("-t", "k3s"))

        // then
        assertFalse(cmd.isValid())
        assertEquals(CliK3sArgumentsParser.K3sType.K3S, cmd.type())
    }

    @Test
    fun test_parseServerArguments_has_default_type_k3s() {
        // when
        val cmd = parseServerArguments(args = arrayOf("-l"))

        // then
        assertTrue(cmd.isValid())
        assertEquals(CliK3sArgumentsParser.K3sType.K3S, cmd.type())
    }

    @Test
    fun test_parseServerArguments_are_valid_for_k3d() {
        // when
        val cmd = parseServerArguments(args = arrayOf("-l", "-t", "k3d"))

        // then
        assertTrue(cmd.isValid())
        assertEquals(CliK3sArgumentsParser.K3sType.K3D, cmd.type())
    }
}