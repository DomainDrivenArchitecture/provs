package org.domaindrivenarchitecture.provs.configuration.infrastructure

import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFileName
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
        val invalidFileName = ApplicationFileName("iDontExist")
        val repo: ApplicationFileRepository = DefaultApplicationFileRepository()

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
        val validFileName = "src/test/resources/existing_file"

        // when
        val validFile = ApplicationFileName(File(validFileName).path)
        val repo: ApplicationFileRepository = DefaultApplicationFileRepository()
        repo.assertExists(validFile)

        // then
        // no exception is thrown
    }
}
