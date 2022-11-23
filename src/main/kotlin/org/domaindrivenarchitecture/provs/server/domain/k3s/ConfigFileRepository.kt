package org.domaindrivenarchitecture.provs.server.domain.k3s

interface ConfigFileRepository {
    fun assertExists(configFileName: ConfigFileName?)
}