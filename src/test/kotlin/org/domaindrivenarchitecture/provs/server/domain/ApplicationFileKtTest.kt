package org.domaindrivenarchitecture.provs.server.domain

import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.configuration.infrastructure.DefaultConfigFileRepository
import org.domaindrivenarchitecture.provs.framework.core.getLocalFileContent
import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFile
import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFileName
import org.domaindrivenarchitecture.provs.server.infrastructure.DefaultApplicationFileRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

internal class ApplicationFileKtTest {

    @Test
    fun assertValidateReturnsSpecErrors() {
        // given
        val applicationFileName = ApplicationFileName("src/test/resources/failed-spec.yaml")

        // when
        val file = ApplicationFile(applicationFileName, getLocalFileContent(applicationFileName.fullyQualifiedName()))

        // then
        val result = file.validate()

        assertEquals(arrayListOf("Spec failed"), result)
    }

    @Test
    fun assertValidateReturnsJavaErrors() {
        // given
        val applicationFileName = ApplicationFileName("src/test/resources/java-exception.yaml")

        // when
        val file = ApplicationFile(applicationFileName, getLocalFileContent(applicationFileName.fullyQualifiedName()))

        // then
        val result = file.validate()

        assertEquals(arrayListOf("Exception in thread"), result)
    }
}