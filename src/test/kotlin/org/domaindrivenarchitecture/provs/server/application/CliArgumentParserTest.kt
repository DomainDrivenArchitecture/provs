package org.domaindrivenarchitecture.provs.server.application

import org.domaindrivenarchitecture.provs.configuration.domain.TargetCliCommand
import org.domaindrivenarchitecture.provs.server.domain.ServerType
import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFileName
import org.domaindrivenarchitecture.provs.server.domain.k3s.K3sCliCommand
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class CliArgumentParserTest {

    @Test
    fun test_parseServerArguments_are_valid_for_k3s() {
        // given
        val parser = CliArgumentsParser("test")

        // when
        val result = parser.parseCommand(args = arrayOf("k3s", "local", "-c", "config.yaml"))

        // then
        assertTrue(result.isValidServerType())
        assertTrue(result.isValidTarget())
        assertTrue(result.isValidConfigFileName())
    }

    @Test
    fun test_parseServerArguments_are_valid_for_k3s_withOnly_grafana() {
        // given
        val parser = CliArgumentsParser("test")

        // when
        val result: K3sCliCommand = parser.parseCommand(args = arrayOf("k3s", "local", "-o", "grafana")) as K3sCliCommand

        // then
        assertTrue(result.isValidServerType())
        assertTrue(result.isValidTarget())
        assertTrue(result.isValidConfigFileName())
        assertEquals(listOf("grafana"), result.submodules)
        assertEquals(TargetCliCommand("local"), result.target)
    }

    @Test
    fun test_parseServerArguments_are_valid_for_k3s_remote_with_application_yaml() {
        // given
        val parser = CliArgumentsParser("test")

        // when
        val result: K3sCliCommand = parser.parseCommand(args = arrayOf("k3s", "user@host.com", "-a", "app.yaml")) as K3sCliCommand

        // then
        assertTrue(result.isValidTarget())
        assertTrue(result.isValidServerType())
        assertEquals(ApplicationFileName("app.yaml"), result.applicationFileName)
        assertEquals(TargetCliCommand("user@host.com"), result.target)
    }
}