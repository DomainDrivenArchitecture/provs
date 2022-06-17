package org.domaindrivenarchitecture.provs.syspec.infrastructure

import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.framework.core.readFromFile
import org.domaindrivenarchitecture.provs.framework.core.toYaml
import org.domaindrivenarchitecture.provs.framework.core.yamlToType
import org.domaindrivenarchitecture.provs.syspec.domain.SyspecConfig
import java.io.File
import java.io.FileWriter

private const val DEFAULT_CONFIG_FILE = "syspec-config.yaml"

// ---------------------------------  read  ----------------------------------
internal fun findSpecConfigFromFile(file: ConfigFileName? = null): Result<SyspecConfig> = runCatching {
    val filePath = file?.fileName ?: DEFAULT_CONFIG_FILE
    if ((filePath == DEFAULT_CONFIG_FILE) && !File(filePath).exists()) {
        // use default ide config
        findSpecConfigFromResource("syspec/syspec-ide-config.yaml")
    }
    readFromFile(filePath).yamlToType()
}


internal fun findSpecConfigFromResource(resourcePath: String): Result<SyspecConfig> = runCatching {
    val resource = Thread.currentThread().contextClassLoader.getResource(resourcePath)
    requireNotNull(resource) { "Resource $resourcePath not found" }
    resource.readText().yamlToType()
}


// ---------------------------------   write  ----------------------------------
@Suppress("unused")
internal fun writeSpecConfigToFile(fileName: String = DEFAULT_CONFIG_FILE, config: SyspecConfig) =
    FileWriter(fileName).use { it.write(config.toYaml()) }