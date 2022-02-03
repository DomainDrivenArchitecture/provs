package org.domaindrivenarchitecture.provs.desktop.infrastructure

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.json.Json
import org.domaindrivenarchitecture.provs.framework.core.tags.Api
import org.domaindrivenarchitecture.provs.desktop.domain.DesktopConfig
import java.io.BufferedReader
import java.io.FileReader
import java.io.FileWriter


/**
 * Returns WorkplaceConfig; data for config is read from specified file.
 * Throws exceptions FileNotFoundException, SerializationException if file is not found resp. cannot be parsed.
 */
internal fun getConfig(filename: String = "WorkplaceConfig.yaml"): DesktopConfig {

    // read file
    val inputAsString = BufferedReader(FileReader(filename)).use { it.readText() }

    // deserializing
    val config =
            if (filename.lowercase().endsWith(".yaml")) {
                Yaml.default.decodeFromString(DesktopConfig.serializer(), inputAsString)
            } else {
                Json.decodeFromString(DesktopConfig.serializer(), inputAsString)
            }
    return config
}

@Api
internal fun writeConfig(config: DesktopConfig, fileName: String = "WorkplaceConfigExample.yaml") {
    if (fileName.lowercase().endsWith(".yaml")) {
        FileWriter(fileName).use {
            it.write(
                Yaml.default.encodeToString(
                    DesktopConfig.serializer(),
                    config
                )
            )
        }
    } else {
        FileWriter(fileName).use { it.write(Json.encodeToString(DesktopConfig.serializer(), config)) }
    }
}