package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.getLocalFileContent
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkLocalFile
import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFile
import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFileName
import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFileRepository


class DefaultApplicationFileRepository(val applicationFileName: ApplicationFileName) : ApplicationFileRepository {

    private fun assertExists(applicationFileName: String) {
        if (!checkLocalFile(applicationFileName)) {
            throw RuntimeException("File [$applicationFileName] not found. Please check if path is correct.")
        }
    }

    override fun getFile(): ApplicationFile {
        assertExists(applicationFileName.absoluteFileName())

        val applicationFileContents = getLocalFileContent(applicationFileName.absoluteFileName())
        val applicationFile = ApplicationFile(applicationFileName, applicationFileContents)

        return if (applicationFile.isValid()) {
            applicationFile
        } else {
            throw RuntimeException("Application file was invalid.")
        }
    }
}
