package org.domaindrivenarchitecture.provs.desktop.domain

import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.deleteFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base.addKnownHost
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base.isKnownHost
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


class KnownHostTest {

    @ContainerTest
    fun defaultKnownHosts() {
        // given
        val prov = defaultTestContainer()
        prov.task {
            aptInstall("ssh")
            deleteFile("~/.ssh/known_hosts")
        }

        // when
        val res = prov.addKnownHosts()

        // then
        assertTrue(res.success)
    }


    // Subclass of KnownHost for test knownHostSubclass_includes_additional_host
    class KnownHostsSubclass(hostName: String, port: Int?, hostKeys: List<HostKey>): KnownHost(hostName, port, hostKeys) {

        companion object {
            val ANOTHER_HOST = KnownHostsSubclass("anotherhost.com", 2222, listOf("key1"))

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

    @ContainerTest
    fun knownHost_with_port_verified_successfully() {
        // given
        val prov = defaultTestContainer()
        prov.task {
            aptInstall("ssh")
            deleteFile("~/.ssh/known_hosts")
        }

        // when
        assertFalse(prov.isKnownHost(KnownHost.GITHUB.hostName))
        assertFalse(prov.isKnownHost(KnownHost.GITHUB.hostName, 22))
        val res = prov.addKnownHost(KnownHost(KnownHost.GITHUB.hostName, 22, KnownHost.GITHUB.hostKeys), verifyKeys = true)

        // then
        assertTrue(res.success)
        assertFalse(prov.isKnownHost(KnownHost.GITHUB.hostName))
        assertTrue(prov.isKnownHost(KnownHost.GITHUB.hostName, 22))
    }

    @ContainerTest
    fun knownHost_with_port_verification_failing() {
        // given
        val prov = defaultTestContainer()
        prov.task {
            aptInstall("ssh")
            deleteFile("~/.ssh/known_hosts")
        }

        // when
        assertFalse(prov.isKnownHost(KnownHost.GITHUB.hostName, 80))
        val res2 = prov.addKnownHost(KnownHost(KnownHost.GITHUB.hostName, 80, KnownHost.GITHUB.hostKeys), verifyKeys = true)

        // then
        assertFalse(res2.success)
        assertFalse(prov.isKnownHost(KnownHost.GITHUB.hostName))
        assertFalse(prov.isKnownHost(KnownHost.GITHUB.hostName, 80))
    }
}

