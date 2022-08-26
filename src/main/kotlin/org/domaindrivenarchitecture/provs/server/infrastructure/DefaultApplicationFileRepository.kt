package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFileName
import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFileRepository

class DefaultApplicationFileRepository : ApplicationFileRepository {

    override fun exists(applicationFileName: ApplicationFileName?): Boolean {
        if (applicationFileName == null) {
            return true
        }
        return genericFileExistenceCheck(applicationFileName.fileName)
    }

}
