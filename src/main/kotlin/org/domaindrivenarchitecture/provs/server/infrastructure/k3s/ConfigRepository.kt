package org.domaindrivenarchitecture.provs.server.infrastructure.k3s

import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.framework.core.readFromFile
import org.domaindrivenarchitecture.provs.framework.core.yamlToType
import org.domaindrivenarchitecture.provs.server.domain.k3s.K3sConfig
import org.domaindrivenarchitecture.provs.server.domain.k3s.Node
import java.io.File

private const val DEFAULT_CONFIG_FILE = "server-config.yaml"

fun getK3sConfig(fileName: ConfigFileName?): K3sConfig {
    val filename = fileName?.fileName ?: DEFAULT_CONFIG_FILE
    return if (File(filename).exists()) {
        readFromFile(filename).yamlToType()
    } else {
        K3sConfig("localhost", Node("127.0.0.1"), apple = true)
    }
}
