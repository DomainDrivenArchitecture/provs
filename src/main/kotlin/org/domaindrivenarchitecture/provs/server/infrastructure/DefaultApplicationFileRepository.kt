package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFileName
import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFileRepository

class DefaultApplicationFileRepository : ApplicationFileRepository {

    override fun exists(applicationFileName: ApplicationFileName?) {
        if (applicationFileName != null) {
            if (!genericFileExistenceCheck(applicationFileName.fullqualified())) {
                throw RuntimeException("Application file not found. Please check if path is correct.")
            }
        }
    }
}
