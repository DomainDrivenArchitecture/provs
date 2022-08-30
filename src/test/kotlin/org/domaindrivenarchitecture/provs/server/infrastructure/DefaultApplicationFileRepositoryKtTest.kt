package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFileName
import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFileRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.File

internal class DefaultApplicationFileRepositoryKtTest {

    @Test
    fun assertExistsThrowsRuntimeException() {
        //when
        val invalidFileName: ApplicationFileName = ApplicationFileName("iDontExist")
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
        //when
        val validFileName = "iExist"
        File(validFileName).createNewFile()

        val validFile: ApplicationFileName =
        ApplicationFileName(File(validFileName).absolutePath)
        val repo: ApplicationFileRepository = DefaultApplicationFileRepository()

        // then
        repo.assertExists(validFile)

        File(validFileName).deleteOnExit()
    }
}