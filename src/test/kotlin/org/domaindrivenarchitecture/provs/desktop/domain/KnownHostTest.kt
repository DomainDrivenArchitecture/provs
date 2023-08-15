package org.domaindrivenarchitecture.provs.desktop.domain

import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.deleteFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base.KNOWN_HOSTS_FILE
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


class KnownHostTest {

    @ContainerTest
    fun defaultKnownHosts() {
        // given
        val prov = defaultTestContainer()
        prov.task {
            aptInstall("ssh")
            deleteFile(KNOWN_HOSTS_FILE)
        }

        // when
        val res = prov.addKnownHosts()

        // then
        assertTrue(res.success)
    }


    // Subclass of KnownHost for test knownHostSubclass_includes_additional_host
    class KnownHostsSubclass(hostName: String, hostKeys: List<HostKey>): KnownHost(hostName, hostKeys) {

        companion object {
            val ANOTHER_HOST = KnownHostsSubclass("anotherhost.com", listOf("key1"))

            fun values(): List<KnownHost> {
                return values + ANOTHER_HOST
            }
        }
    }

    @Test
    fun knownHostSubclass_includes_additional_host() {
        // when
        val hosts = KnownHostsSubclass.values()

        // then
        assertTrue(hosts.size > 1)
        assertEquals("key1", hosts.last().hostKeys[0])
    }
}

