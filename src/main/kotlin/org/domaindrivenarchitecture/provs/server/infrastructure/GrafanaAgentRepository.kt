package org.domaindrivenarchitecture.provs.server.infrastructure

import com.charleskorn.kaml.MissingRequiredPropertyException
import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.framework.core.readFromFile
import org.domaindrivenarchitecture.provs.framework.core.toYaml
import org.domaindrivenarchitecture.provs.framework.core.yamlToType
import org.domaindrivenarchitecture.provs.server.domain.k8s_grafana_agent.GrafanaAgentConfigHolder
import org.domaindrivenarchitecture.provs.server.domain.k8s_grafana_agent.GrafanaAgentConfig
import java.io.File
import java.io.FileWriter


private const val DEFAULT_CONFIG_FILE = "server-config.yaml"


fun findK8sGrafanaConfig(fileName: ConfigFileName? = null): GrafanaAgentConfig? {
    val filePath = fileName?.fileName ?: DEFAULT_CONFIG_FILE

    return if (File(filePath).exists()) {
        try {
            readFromFile(filePath).yamlToType<GrafanaAgentConfigHolder>().grafana
        } catch (e: MissingRequiredPropertyException) {
            if (e.message.contains("Property 'grafana'")) null else throw e
        }
    } else {
        null
    }
}


@Suppress("unused")
internal fun writeConfig(config: GrafanaAgentConfigHolder, fileName: String = "grafana-config.yaml") =
    FileWriter(fileName).use { it.write(config.toYaml()) }
