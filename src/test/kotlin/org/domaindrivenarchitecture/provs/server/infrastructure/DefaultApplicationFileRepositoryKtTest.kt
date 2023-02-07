package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.configuration.infrastructure.DefaultConfigFileRepository
import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFileName
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
        val validFileName = "src/test/resources/existing-file"

        // when
        val validFile = ConfigFileName(File(validFileName).path)
        val repo = DefaultConfigFileRepository()
        repo.assertExists(validFile)

        // then
        // no exception is thrown
    }

    @Test
    fun assertC4kSpecErrorThrows() {
        // given
        val applicationFileName = "src/test/resources/failed-spec.yaml"

        // when
        val failedFile = ApplicationFileName(File(applicationFileName).path)
        val repo = DefaultApplicationFileRepository()

        // then
        val exception = assertThrows<RuntimeException>(
            "Should throw because of bad spec."
        ) { repo.assertC4kSpecError(failedFile) }

        assertEquals(
            "Application file src/test/resources/failed-spec.yaml contains spec errors. Please check your configuration file.",
            exception.message)
    }

    @Test
    fun assertC4kSpecErrorPasses() {
        // given
        val validFileName = "src/test/resources/valid.yaml"

        // when
        val validFile = ApplicationFileName(File(validFileName).path)
        val repo = DefaultApplicationFileRepository()
        repo.assertC4kSpecError(validFile)

        // then
        // no exception is thrown
    }

    @Test
    fun assertC4kJavaExceptionThrows() {
        // given
        val applicationFileName = "src/test/resources/java-exception.yaml"

        // when
        val failedFile = ApplicationFileName(File(applicationFileName).path)
        val repo = DefaultApplicationFileRepository()

        // then
        val exception = assertThrows<RuntimeException>(
            "Should throw because of java exception."
        ) { repo.assertC4kJavaException(failedFile) }

        assertEquals(
            "Application file src/test/resources/java-exception.yaml contains java exception. Please check the c4k code for errors.",
            exception.message)
    }

    @Test
    fun assertC4kJavaExceptionPasses() {
        // given
        val validFileName = "src/test/resources/valid.yaml"

        // when
        val validFile = ApplicationFileName(File(validFileName).path)
        val repo = DefaultApplicationFileRepository()
        repo.assertC4kJavaException(validFile)

        // then
        // no exception is thrown
    }
}