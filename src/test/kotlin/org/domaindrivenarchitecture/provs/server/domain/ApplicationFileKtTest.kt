package org.domaindrivenarchitecture.provs.server.domain

import org.domaindrivenarchitecture.provs.framework.core.getLocalFileContent
import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFile
import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFileName
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

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