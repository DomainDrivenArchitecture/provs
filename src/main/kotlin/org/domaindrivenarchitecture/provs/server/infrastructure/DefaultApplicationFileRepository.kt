package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.getLocalFileContent
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkLocalFile
import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFile
import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFileName
import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFileRepository
import java.io.File

class DefaultApplicationFileRepository(val applicationFileName: ApplicationFileName?) : ApplicationFileRepository {

    private fun assertExists(applicationFileName: String?) {
        if (applicationFileName != null && !checkLocalFile(applicationFileName)) {
            throw RuntimeException("Application file not found. Please check if path is correct.")
        }
    }
    override fun getFile() : ApplicationFile {
        assertExists(applicationFileName!!.fullyQualifiedName())

        val applicationFileContents =  getLocalFileContent(applicationFileName.fullyQualifiedName())
        val applicationFile = ApplicationFile(applicationFileName, applicationFileContents)

        return if (applicationFile.isValid()) { applicationFile }
               else { throw RuntimeException("Application file was invalid.") }
    }
}
