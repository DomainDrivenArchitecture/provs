package org.domaindrivenarchitecture.provs.framework.core

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.serializer
import java.io.BufferedReader
import java.io.File
import java.io.FileReader


fun readFromFile(fileName: String): String {
    return BufferedReader(FileReader(fileName)).use { it.readText() }
}

fun writeToFile(fileName: String, text: String) {
    File(fileName).writeText(text)
}


inline fun <reified T : Any> String.yamlToType() = Yaml(configuration = YamlConfiguration(strictMode = false)).decodeFromString(
    serializer<T>(),
    this
)


inline fun <reified T : Any> T.toYaml() = Yaml(configuration = YamlConfiguration(strictMode = false, encodeDefaults = false)).encodeToString(
    serializer<T>(),
    this
)
