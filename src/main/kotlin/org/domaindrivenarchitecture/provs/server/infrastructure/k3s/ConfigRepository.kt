package org.domaindrivenarchitecture.provs.server.infrastructure.k3s

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.json.Json
import org.domaindrivenarchitecture.provs.server.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.server.domain.k3s.*
import java.io.BufferedReader
import java.io.FileReader


public fun getK3sConfig(configFileName: ConfigFileName): K3sConfig {
    // read file
    val inputAsString = BufferedReader(FileReader(configFileName.fileName)).use { it.readText() }

    // deserializing
    val config =
        if (configFileName.fileName.lowercase().endsWith(".yaml")) {
            Yaml.default.decodeFromString(K3sSerial.serializer(), inputAsString)
        } else {
            Json.decodeFromString(K3sSerial.serializer(), inputAsString)
        }
    return K3sConfig(
        Fqdn(config.fqdn),
        Ipv4(config.nodeIpv4),
        config.nodeIpv6?.let { Ipv6(it) },
        config.loopbackIpv4?.let { Ipv4(it) },
        config.loopbackIpv6?.let { Ipv6(it) },
        config.reprovision?.let { Reprovision(it) }
    )
}