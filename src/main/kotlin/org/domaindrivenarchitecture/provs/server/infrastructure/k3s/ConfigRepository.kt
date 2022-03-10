package org.domaindrivenarchitecture.provs.server.infrastructure.k3s

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
    val filename = fileName?.fileName ?: DEFAULT_CONFIG_FILE

    if ((filename.substringAfterLast("/") == DEFAULT_CONFIG_FILE) && !File(filename).exists()) {
        writeK3sConfig(ConfigFileName(filename), K3sConfig("localhost", Node("127.0.0.1"), apple = true))
    }
    return readFromFile(filename).yamlToType()
}

fun writeK3sConfig(fileName: ConfigFileName, config: K3sConfig) {
    writeToFile(fileName.fileName, config.toYaml())
}
fun main() {
    getK3sConfig()
}