package io.provs.ubuntu.extensions.workplace

import io.provs.ubuntu.keys.KeyPairSource
import io.provs.ubuntu.secret.SecretSource
import io.provs.ubuntu.secret.SecretSourceType
import io.provs.ubuntu.secret.SecretSupplier
import io.provs.ubuntu.secret.secretSources.PlainSecretSource
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.*


@Serializable
class WorkplaceConfig(
    val type: WorkplaceType = WorkplaceType.MINIMAL,
    val ssh: KeyPairSource? = null,
    val gpg: KeyPairSource? = null,
    val gitUserName: String? = null,
    val gitEmail: String? = null,
)


// -------------------------------------------- file methods ------------------------------------
fun readWorkplaceConfigFromFile(filename: String = "WorkplaceConfig.json"): WorkplaceConfig? {
    val file = File(filename)
    return if (file.exists())
        try {
            // read from file
            val inputAsString = BufferedReader(FileReader(filename)).use { it.readText() }

            return Json.decodeFromString(WorkplaceConfig.serializer(), inputAsString)
        } catch (e: FileNotFoundException) {
            null
        } else null
}


fun writeWorkplaceConfigToFile(config: WorkplaceConfig) {
    val fileName = "WorkplaceConfig.json"

    FileWriter(fileName).use { it.write(Json.encodeToString(WorkplaceConfig.serializer(), config)) }
}
