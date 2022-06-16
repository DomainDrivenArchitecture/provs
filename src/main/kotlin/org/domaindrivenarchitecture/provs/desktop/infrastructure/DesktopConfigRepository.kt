package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.desktop.domain.DesktopConfig
import org.domaindrivenarchitecture.provs.framework.core.readFromFile
import org.domaindrivenarchitecture.provs.framework.core.toYaml
import org.domaindrivenarchitecture.provs.framework.core.yamlToType
import java.io.FileWriter


/**
 * Returns DesktopConfig; data for config is read from specified file.
 * Throws exceptions FileNotFoundException, SerializationException if file is not found resp. cannot be parsed.
 */
internal fun getConfig(filename: String = "desktop-config.yaml"): DesktopConfig = readFromFile(filename).yamlToType()


@Suppress("unused")
internal fun writeConfig(config: DesktopConfig, fileName: String = "desktop-config.yaml") = FileWriter(fileName).use { it.write(config.toYaml()) }
