package org.domaindrivenarchitecture.provs.server.infrastructure

import com.charleskorn.kaml.MissingRequiredPropertyException
import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.framework.core.readFromFile
import org.domaindrivenarchitecture.provs.framework.core.toYaml
import org.domaindrivenarchitecture.provs.framework.core.yamlToType
import org.domaindrivenarchitecture.provs.server.domain.hetzner_csi.HetznerCSIConfig
import org.domaindrivenarchitecture.provs.server.domain.hetzner_csi.HetznerCSIConfigHolder
import java.io.File
import java.io.FileWriter

private const val DEFAULT_CONFIG_FILE = "server-config.yaml"

fun findHetznerCSIConfig(fileName: ConfigFileName? = null): HetznerCSIConfig? {
    val filePath = fileName?.fileName ?: DEFAULT_CONFIG_FILE

    return if(File(filePath).exists()) {
        try {
            readFromFile(filePath).yamlToType<HetznerCSIConfigHolder>().hetzner
        } catch (e: MissingRequiredPropertyException) {
            if (e.message.contains("Property 'hetzner'")) null else throw e
        }
    } else {
        null
    }
}

@Suppress("unused")
internal fun writeConfig(config: HetznerCSIConfigHolder, fileName: String = "hetzner-config.yaml") =
    FileWriter(fileName).use { it.write(config.toYaml()) }