package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.configuration.infrastructure.DefaultConfigFileRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

internal class DefaultApplicationFileRepositoryKtTest {

    @Test
    fun assertExistsThrowsRuntimeException() {
        // when
        val invalidFileName = ConfigFileName("iDontExist")
        val repo = DefaultConfigFileRepository()

        // then
        val exception = assertThrows<RuntimeException>(
            "Should not find the file."
        ) { repo.assertExists(invalidFileName) }

        assertEquals(
            "Config file iDontExist not found. Please check if path is correct.",
            exception.message)
    }

    @Test
    fun assertExistsPasses() {
        // given
        val validFileName = "src/test/resources/existing_file"

        // when
        val validFile = ConfigFileName(File(validFileName).path)
        val repo = DefaultConfigFileRepository()
        repo.assertExists(validFile)

        // then
        // no exception is thrown
    }
}