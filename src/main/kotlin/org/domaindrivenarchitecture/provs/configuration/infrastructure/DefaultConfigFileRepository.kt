package org.domaindrivenarchitecture.provs.configuration.infrastructure

import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.checkLocalFile
import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileRepository

class DefaultConfigFileRepository : ConfigFileRepository {
    override fun assertExists(configFileName: ConfigFileName?) {
        if (configFileName != null && !checkLocalFile(configFileName.absolutePath())) {
            throw RuntimeException("Config file not found. Please check if path is correct.")
        }
    }
}
