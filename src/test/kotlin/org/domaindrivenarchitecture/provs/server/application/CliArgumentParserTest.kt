package org.domaindrivenarchitecture.provs.server.application

import org.domaindrivenarchitecture.provs.server.domain.ServerType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class CliArgumentParserTest {

    @Test
    fun test_parseServerArguments_are_valid_for_k3s() {
        // given
        val parser = CliArgumentsParser("test")

        // when
        val result = parser.parseCommand(args = arrayOf("k3s", "-l", "config.yaml"))

        // then
        assertTrue(result.isValid())
        assertEquals(ServerType.K3S, result.serverType)
    }
}