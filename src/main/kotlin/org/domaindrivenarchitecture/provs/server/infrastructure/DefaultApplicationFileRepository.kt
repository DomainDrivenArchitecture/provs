package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkLocalFile
import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFileName
import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFileRepository

class DefaultApplicationFileRepository : ApplicationFileRepository {

    override fun assertExists(applicationFileName: ApplicationFileName?) {
        if (applicationFileName != null && !checkLocalFile(applicationFileName.fullqualified())) {
            throw RuntimeException("Application file ${applicationFileName.fileName} not found. Please check if path is correct.")
        }
    }
}
