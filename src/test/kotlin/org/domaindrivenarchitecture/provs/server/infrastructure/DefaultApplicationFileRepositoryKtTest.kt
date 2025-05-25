package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFileName
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DefaultApplicationFileRepositoryKtTest {

    @Test
    fun assertExistsThrowsRuntimeException() {
        // when
        val invalidFileName = ApplicationFileName("iDontExist")
        val repo = DefaultApplicationFileRepository(invalidFileName)

        // then
        val exception = assertThrows<RuntimeException>(
            "Should not find the file."
        ) { repo.getFile() }

        assertTrue(exception.message?.matches(Regex("File \\[.*iDontExist] not found. Please check if path is correct.")) ?: false, "Exception message wrong: $exception.message")
    }

    @Test
    fun assertGetFileThrowsRuntimeException() {
        // when
        val invalidFileName = ApplicationFileName("src/test/resources/java-exception.yaml")
        val repo = DefaultApplicationFileRepository(invalidFileName)

        // then
        val exception = assertThrows<RuntimeException>(
            "Should not find the file."
        ) { repo.getFile() }

        assertEquals(
            "Application file was invalid.",
            exception.message)
    }
}