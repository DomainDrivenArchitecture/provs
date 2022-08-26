package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFileName
import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFileRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.File
import java.nio.file.Paths

internal class DefaultApplicationFileRepositoryKtTest {

    @Test
    fun existsThrowsRuntimeException() {
        //when
        val invalidFileName: ApplicationFileName = ApplicationFileName("iDontExist")
        val repo: ApplicationFileRepository = DefaultApplicationFileRepository()

        // then
        val exception = assertThrows<RuntimeException>(
            "Should not find the file."
        ) { repo.exists(invalidFileName) }

        assertEquals(
            "Application file not found. Please check if path is correct.",
            exception.message)
    }

    @Test
    fun existsPasses() {
        //when
        val validFileName = "iExist"
        File(validFileName).createNewFile()

        val validFile: ApplicationFileName =
        ApplicationFileName(File(validFileName).absolutePath)
        val repo: ApplicationFileRepository = DefaultApplicationFileRepository()

        // then
        repo.exists(validFile)

        File(validFileName).deleteOnExit()
    }
}