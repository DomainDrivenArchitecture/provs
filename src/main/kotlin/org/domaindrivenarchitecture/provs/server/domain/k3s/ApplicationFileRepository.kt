package org.domaindrivenarchitecture.provs.server.domain.k3s

interface ApplicationFileRepository {
    fun assertExists(applicationFileName: ApplicationFileName?)
    fun assertC4kSpecError(applicationFileName: ApplicationFileName?)
    fun assertC4kJavaException(applicationFileName: ApplicationFileName?)
}