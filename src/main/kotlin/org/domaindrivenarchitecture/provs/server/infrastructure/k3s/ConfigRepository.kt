package org.domaindrivenarchitecture.provs.server.infrastructure.k3s

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.json.Json
import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.server.domain.k3s.*
import java.io.BufferedReader
import java.io.FileReader


public fun getK3sConfig(configFileName: ConfigFileName): K3sConfig {
    // read file
    val inputAsString = BufferedReader(FileReader(configFileName.fileName)).use { it.readText() }

    // deserializing
    val config =
        if (configFileName.fileName.lowercase().endsWith(".yaml")) {
            Yaml.default.decodeFromString(K3sConfig.serializer(), inputAsString)
        } else {
            Json.decodeFromString(K3sConfig.serializer(), inputAsString)
        }
    return config
}