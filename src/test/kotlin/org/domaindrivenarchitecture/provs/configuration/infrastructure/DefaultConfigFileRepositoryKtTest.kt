package org.domaindrivenarchitecture.provs.configuration.infrastructure

import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileRepository
import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFile
import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFileRepository
import org.domaindrivenarchitecture.provs.server.infrastructure.DefaultApplicationFileRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.File

internal class DefaultConfigFileRepositoryKtTest {
    @Test
    fun assertExistsThrowsRuntimeException() {
        // when
        val invalidFileName = ConfigFileName("iDontExist")
        val repo: ConfigFileRepository = DefaultConfigFileRepository()

        // then
        val exception = assertThrows<RuntimeException>(
            "Should not find the file."
        ) { repo.assertExists(invalidFileName) }

        assertEquals(
            "Application file iDontExist not found. Please check if path is correct.",
            exception.message)
    }

    @Test
    fun assertExistsPasses() {
        // given
        val validFileName = "src/test/resources/existing-file"

        // when
        val validFile = ConfigFileName(File(validFileName).path)
        val repo: ConfigFileRepository = DefaultConfigFileRepository()
        repo.assertExists(validFile)

        // then
        // no exception is thrown
    }
}
