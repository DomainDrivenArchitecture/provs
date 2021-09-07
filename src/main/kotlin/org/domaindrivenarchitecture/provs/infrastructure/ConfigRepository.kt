package org.domaindrivenarchitecture.provs.infrastructure

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.json.Json
import org.domaindrivenarchitecture.provs.core.tags.Api
import org.domaindrivenarchitecture.provs.domain.WorkplaceConfig
import java.io.*


internal fun getConfig(filename: String = "WorkplaceConfig.json"): WorkplaceConfig {
    val file = File(filename)
    require(file.exists(), { "File not found: " + filename })

    val config =
        try {
            // read from file
            val inputAsString = BufferedReader(FileReader(filename)).use { it.readText() }

            // serializing objects
            if (filename.lowercase().endsWith(".yaml")) {
                Yaml.default.decodeFromString(WorkplaceConfig.serializer(), inputAsString)
            } else {
                Json.decodeFromString(WorkplaceConfig.serializer(), inputAsString)
            }
        } catch (e: FileNotFoundException) {
            throw IllegalArgumentException("File not found: " + filename, e)
        }
    return config
}

@Api
internal fun writeConfig(config: WorkplaceConfig, fileName: String = "WorkplaceConfig.yaml") {
    if (fileName.lowercase().endsWith(".yaml")) {
        FileWriter(fileName).use {
            it.write(
                Yaml.default.encodeToString(
                    WorkplaceConfig.serializer(),
                    config
                )
            )
        }
    } else {
        FileWriter(fileName).use { it.write(Json.encodeToString(WorkplaceConfig.serializer(), config)) }
    }
}