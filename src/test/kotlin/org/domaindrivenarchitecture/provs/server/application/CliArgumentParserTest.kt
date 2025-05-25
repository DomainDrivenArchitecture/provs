package org.domaindrivenarchitecture.provs.server.application

import org.domaindrivenarchitecture.provs.configuration.domain.TargetCliCommand
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
        assertTrue(result.isValidTarget())
    }

    @Test
    fun test_parseServerArguments_are_valid_for_k3s_withOnly_grafana() {
        // given
        val parser = CliArgumentsParser("test")

        // when
        val result: K3sCliCommand = parser.parseCommand(args = arrayOf("k3s", "local", "-o", "grafana")) as K3sCliCommand

        // then
        assertTrue(result.isValidTarget())
        assertEquals(listOf("grafana"), result.onlyModules)
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
        assertEquals(ApplicationFileName("app.yaml").fullyQualifiedName(), result.applicationFileNames?.get(0)?.fullyQualifiedName())
        assertEquals(TargetCliCommand("user@host.com"), result.target)
    }

    @Test
    fun test_parseServerArguments_are_valid_for_k3s_remote_with_multiple_application_yaml() {
        // given
        val parser = CliArgumentsParser("test")

        // when
        val result: K3sCliCommand = parser.parseCommand(args = arrayOf("k3s", "user@host.com", "-a", "app.yaml,app2.yaml")) as K3sCliCommand

        // then
        assertTrue(result.isValidTarget())
        assertEquals(listOf(ApplicationFileName("app.yaml"), ApplicationFileName("app2.yaml")).toString(), result.applicationFileNames.toString())
        assertEquals(TargetCliCommand("user@host.com"), result.target)
    }
}