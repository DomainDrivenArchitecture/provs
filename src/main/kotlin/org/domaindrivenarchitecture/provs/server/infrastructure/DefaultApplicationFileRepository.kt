package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkLocalFile
import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFileName
import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFileRepository
import java.io.File

class DefaultApplicationFileRepository : ApplicationFileRepository {

    override fun assertExists(applicationFileName: ApplicationFileName?) {
        if (applicationFileName != null && !checkLocalFile(applicationFileName.fullqualified())) {
            throw RuntimeException("Application file ${applicationFileName.fileName} not found. Please check if path is correct.")
        }
    }

    override fun assertC4kSpecError(applicationFileName: ApplicationFileName?) {
        if (applicationFileName != null) {
            val fileContent = File(applicationFileName.fullqualified()).readText()


            if (fileContent.contains("Spec.failed".toRegex()) && fileContent.contains("Detected.*[0-9].*error+".toRegex())) {
                throw RuntimeException("Application file ${applicationFileName.fileName} contains spec errors. Please check your configuration file.")
            }

        }
    }

    override fun assertC4kJavaException(applicationFileName: ApplicationFileName?) {
        if (applicationFileName != null) {
            val fileContent = File(applicationFileName.fullqualified()).readText()


            if (fileContent.contains("Exception.in.thread".toRegex())) {
                throw RuntimeException("Application file ${applicationFileName.fileName} contains java exception. Please check the c4k code for errors.")
            }

        }
    }


}
