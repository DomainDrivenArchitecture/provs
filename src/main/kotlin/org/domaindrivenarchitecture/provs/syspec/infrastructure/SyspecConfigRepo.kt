package org.domaindrivenarchitecture.provs.syspec.infrastructure

import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.framework.core.readFromFile
import org.domaindrivenarchitecture.provs.framework.core.toYaml
import org.domaindrivenarchitecture.provs.framework.core.yamlToType
import org.domaindrivenarchitecture.provs.syspec.domain.CommandSpec
import org.domaindrivenarchitecture.provs.syspec.domain.SpecConfig
import java.io.File
import java.io.FileWriter

private const val DEFAULT_CONFIG_FILE = "syspec-config.yaml"

// ---------------------------------  read  ----------------------------------
internal fun findSpecConfigFromFile(file: ConfigFileName? = null): Result<SpecConfig> = runCatching {
    val filePath = file?.fileName ?: DEFAULT_CONFIG_FILE
    if ((filePath == DEFAULT_CONFIG_FILE) && !File(filePath).exists()) {
        // provide default config
        writeSpecConfigToFile(filePath, SpecConfig(listOf(CommandSpec("echo just_for_demo", "just_for_demo"))))
    }
    readFromFile(filePath).yamlToType<SpecConfig>()
}


internal fun findSpecConfigFromResource(resourcePath: String): Result<SpecConfig> = runCatching {
    val resource = Thread.currentThread().contextClassLoader.getResource(resourcePath)
    requireNotNull(resource) { "Resource $resourcePath not found" }
    resource.readText().yamlToType()
}


// ---------------------------------   write  ----------------------------------
internal fun writeSpecConfigToFile(fileName: String = DEFAULT_CONFIG_FILE, config: SpecConfig) =
    FileWriter(fileName).use { it.write(config.toYaml()) }