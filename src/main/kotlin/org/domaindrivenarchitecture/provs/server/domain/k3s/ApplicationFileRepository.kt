package org.domaindrivenarchitecture.provs.server.domain.k3s

interface ApplicationFileRepository {
    fun getFile() : ApplicationFile

}