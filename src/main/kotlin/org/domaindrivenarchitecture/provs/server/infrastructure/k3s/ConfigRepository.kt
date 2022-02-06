package org.domaindrivenarchitecture.provs.server.infrastructure.k3s

import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.framework.core.readFromFile
import org.domaindrivenarchitecture.provs.framework.core.yamlToType
import org.domaindrivenarchitecture.provs.server.domain.k3s.K3sConfig


private const val DEFAULT_CONFIG_FILE = "ServerConfig.yaml"

fun getK3sConfig(fileName: ConfigFileName?): K3sConfig {
    return readFromFile(fileName?.fileName ?: DEFAULT_CONFIG_FILE).yamlToType()
}

