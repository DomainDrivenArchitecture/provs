package org.domaindrivenarchitecture.provs.framework.core

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import java.io.BufferedReader
import java.io.FileReader


fun readFromFile(fileName: String): String {
    return BufferedReader(FileReader(fileName)).use { it.readText() }
}


@OptIn(InternalSerializationApi::class)
inline fun <reified T : Any> String.yamlToType() = Yaml(configuration = YamlConfiguration(strictMode = false)).decodeFromString(
    T::class.serializer(),
    this
)
