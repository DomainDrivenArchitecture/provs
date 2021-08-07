package io.provs.ubuntu.user

import io.provs.ubuntu.keys.KeyPairSource
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.FileReader
import java.io.FileWriter


@Serializable
class UserConfig(val userName: String, val gitEmail: String? = null, val gpg: KeyPairSource? = null, val ssh: KeyPairSource? = null)


// -------------------------------------------- file methods ------------------------------------
@Suppress("unused")
fun readUserConfigFromFile(filename: String = "UserConfig.json") : UserConfig {
    // read from file
    val inputAsString = BufferedReader(FileReader(filename)).use { it.readText() }

    // serializing objects
    return Json.decodeFromString(UserConfig.serializer(), inputAsString)
}

fun writeUserConfigToFile(config: UserConfig) {
    val fileName = "UserConfig.json"

    FileWriter(fileName).use { it.write(Json.encodeToString(UserConfig.serializer(), config)) }
}
