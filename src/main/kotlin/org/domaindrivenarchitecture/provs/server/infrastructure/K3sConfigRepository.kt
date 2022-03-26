package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.framework.core.readFromFile
import org.domaindrivenarchitecture.provs.framework.core.toYaml
import org.domaindrivenarchitecture.provs.framework.core.writeToFile
import org.domaindrivenarchitecture.provs.framework.core.yamlToType
import org.domaindrivenarchitecture.provs.server.domain.k3s.K3sConfig
import org.domaindrivenarchitecture.provs.server.domain.k3s.Node
import java.io.File


private const val DEFAULT_CONFIG_FILE = "server-config.yaml"


fun getK3sConfig(fileName: ConfigFileName? = null): K3sConfig {
    val filePath = fileName?.fileName ?: DEFAULT_CONFIG_FILE

    // create a default config
    if ((filePath == DEFAULT_CONFIG_FILE) && !File(filePath).exists()) {
        writeK3sConfig(filePath, K3sConfig("localhost", Node("127.0.0.1"), echo = true))
    }

    return readFromFile(filePath).yamlToType()
}


fun writeK3sConfig(filePath: String, config: K3sConfig) = writeToFile(filePath, config.toYaml())

