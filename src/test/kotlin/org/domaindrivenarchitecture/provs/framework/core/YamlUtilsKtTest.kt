package org.domaindrivenarchitecture.provs.framework.core

import org.domaindrivenarchitecture.provs.server.domain.k3s.K3sConfig
import org.domaindrivenarchitecture.provs.server.domain.k3s.Node
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class YamlUtilsKtTest {

    @Test
    fun yamlToType() {
        // when
        val text = """
            fqdn: "host"
            node:
              ipv4: "1.2.3.4"
          """.trimIndent()
        val k3sConf = text.yamlToType<K3sConfig>()

        // then
        assertEquals("host", k3sConf.fqdn)
        assertEquals("1.2.3.4", k3sConf.node.ipv4)
        assertEquals(null, k3sConf.node.ipv6)
    }


    @Test
    fun toYaml() {
        // when
        val yaml = K3sConfig("host", Node("1.2.3.4")).toYaml()

        // then
        val expected = """
            fqdn: "host"
            node:
              ipv4: "1.2.3.4"
              ipv6: null
            loopback:
              ipv4: "192.168.5.1"
              ipv6: "fc00::5:1"
            certmanager: null
            apple: null
            reprovision: false""".trimIndent()

        assertEquals(expected, yaml)
    }
}