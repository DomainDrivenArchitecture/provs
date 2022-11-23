package org.domaindrivenarchitecture.provs.configuration.domain

interface ConfigFileRepository {
    fun assertExists(configFileName: ConfigFileName?)
}