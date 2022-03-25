package org.domaindrivenarchitecture.provs.syspec.infrastructure

import com.charleskorn.kaml.Yaml
import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.framework.core.readFromFile
import org.domaindrivenarchitecture.provs.framework.core.yamlToType
import org.domaindrivenarchitecture.provs.syspec.domain.CommandSpec
import org.domaindrivenarchitecture.provs.syspec.domain.SpecConfig
import java.io.File
import java.io.FileWriter
import java.io.IOException

private const val DEFAULT_CONFIG_FILE = "syspec-config.yaml"

internal fun writeSpecConfigToFile(
    fileName: String = DEFAULT_CONFIG_FILE,
    config: SpecConfig
) {
    FileWriter(fileName).use {
        it.write(
            Yaml.default.encodeToString(
                SpecConfig.serializer(),
                config
            )
        )
    }
}

internal fun getSpecConfigFromFile(file: ConfigFileName? = null): SpecConfig {
    val filename = file?.fileName ?: DEFAULT_CONFIG_FILE

    if ((filename.substringAfterLast("/") == DEFAULT_CONFIG_FILE) && !File(filename).exists()) {
        // provide default config
        writeSpecConfigToFile(filename, SpecConfig(listOf(CommandSpec("echo just_for_demo", "just_for_demo"))))
    }
    return readFromFile(filename).yamlToType()
}

internal fun findSpecConfigFromFile(file: ConfigFileName? = null): SpecConfig? {
    return try {
        val config = getSpecConfigFromFile(file)
        config
    } catch (e: IOException) {
        println("Error: " + e.message)
        null
    }
}

internal fun getSpecConfigFromResource(resourcePath: String): SpecConfig {
    val resource = Thread.currentThread().contextClassLoader.getResource(resourcePath)
    requireNotNull(resource) { "Resource $resourcePath not found" }
    return resource.readText().yamlToType()
}

internal fun findSpecConfigFromResource(resourcePath: String): SpecConfig? {
    return try {
        val config = getSpecConfigFromResource(resourcePath)
        config
    } catch (e: IllegalArgumentException) {
        println("Error: " + e.message)
        null
    }
}
