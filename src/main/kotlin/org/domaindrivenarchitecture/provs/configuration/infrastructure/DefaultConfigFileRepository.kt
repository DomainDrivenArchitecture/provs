package org.domaindrivenarchitecture.provs.configuration.infrastructure

import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkLocalFile
import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileRepository

class DefaultConfigFileRepository : ConfigFileRepository {

    override fun assertExists(configFileName: ConfigFileName?) {
        if (configFileName != null && !checkLocalFile(configFileName.fullqualified())) {
            throw RuntimeException("Config file ${configFileName.fileName} not found. Please check if path is correct.")
        }
    }
}
