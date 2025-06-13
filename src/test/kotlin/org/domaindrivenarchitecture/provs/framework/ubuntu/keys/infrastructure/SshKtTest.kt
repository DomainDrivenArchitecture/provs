package org.domaindrivenarchitecture.provs.framework.ubuntu.keys.infrastructure

import org.domaindrivenarchitecture.provs.desktop.domain.KnownHost
import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.deleteFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.fileContainsText
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.fileContent
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.infrastructure.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.*
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.domain.SshKeyPair
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.*

const val KNOWN_HOSTS_FILE = "~/.ssh/known_hosts"

internal class SshKtTest {
    @ContainerTest
    fun configureSshKeys_for_ssh_type_rsa() {
        // given
        val prov = defaultTestContainer()

        // when
        val res = prov.configureSshKeys(
            SshKeyPair(
                Secret(publicSSHRSASnakeoilKey()),
                Secret(privateSSHRSASnakeoilKey())
            )
        )

        // then
        assertTrue(res.success)

        val publicSshKeyFileContent = prov.fileContent("~/.ssh/id_rsa.pub")
        assertEquals(publicSSHRSASnakeoilKey() + "\n", publicSshKeyFileContent)

        val privateSshKeyFileContent = prov.fileContent("~/.ssh/id_rsa")
        assertEquals(privateSSHRSASnakeoilKey() + "\n", privateSshKeyFileContent)
    }

    @ContainerTest
    fun configureSshKeys_for_ssh_type_ed25519() {
        // given
        val prov = defaultTestContainer()

        // when
        val res = prov.configureSshKeys(
            SshKeyPair(
                Secret(publicED25519SnakeOilKey()),
                Secret(privateED25519SnakeOilKey())
            )
        )

        // then
        assertTrue(res.success)

        val publicSshKeyFileContent = prov.fileContent("~/.ssh/id_ed25519.pub")
        assertEquals(publicED25519SnakeOilKey() + "\n", publicSshKeyFileContent)

        val privateSshKeyFileContent = prov.fileContent("~/.ssh/id_ed25519")
        assertEquals(privateED25519SnakeOilKey() + "\n", privateSshKeyFileContent)
    }

    @ContainerTest
    fun addKnownHost_without_verification() {
        // given
        val prov = defaultTestContainer()
        prov.task {
            aptInstall("ssh")
            deleteFile(KNOWN_HOSTS_FILE)
        }

        // when
        val res = prov.addKnownHost(KnownHost("github.com", listOf("dummyProtocol dummyKey", "dummyProtocol2 dummyKey2", )))
        val res2 = prov.addKnownHost(KnownHost("github.com", listOf("dummyProtocol dummyKey", "dummyProtocol2 dummyKey2", )))

        // then
        assertTrue(res.success)
        assertTrue(prov.fileContainsText(KNOWN_HOSTS_FILE, "github.com dummyProtocol dummyKey"))
        assertTrue(prov.fileContainsText(KNOWN_HOSTS_FILE, "github.com dummyProtocol2 dummyKey2"))

        assertTrue(res2.success)
        val keyCount = prov.cmd("grep -o -i dummyKey2 $KNOWN_HOSTS_FILE | wc -l").out?.trim()
        assertEquals("1", keyCount)
    }

    @ContainerTest
    fun addKnownHost_with_verifications() {
        // given
        val prov = defaultTestContainer()
        prov.task {
            aptInstall("ssh")
            deleteFile(KNOWN_HOSTS_FILE)
        }

        // when
        val res1 = prov.addKnownHost(KnownHost.GITHUB, verifyKeys = true)
        val res2 = prov.addKnownHost(KnownHost.GITHUB, verifyKeys = true)

        val invalidKey = "ssh-ed25519 AAAAC3Nzalwrongkey!!!"
        val res3 = prov.addKnownHost(KnownHost("github.com", listOf(invalidKey )), verifyKeys = true)

        // then
        assertTrue(res1.success)
        assertTrue(prov.fileContainsText(KNOWN_HOSTS_FILE, KnownHost.GITHUB.hostKeys[0]))

        assertTrue(res2.success)
        assertTrue(prov.fileContainsText(KNOWN_HOSTS_FILE, KnownHost.GITHUB.hostKeys[0]))

        assertFalse(res3.success)
        assertFalse(prov.fileContainsText(KNOWN_HOSTS_FILE, invalidKey))
    }

    @ContainerTest
    fun addKnownHost_with_port_without_verifications() {
        // given
        val prov = defaultTestContainer()
        prov.task {
            aptInstall("ssh")
            deleteFile(KNOWN_HOSTS_FILE)
        }

        // when
        val res1 = prov.addKnownHost(KnownHost("myserver.org", 2222, listOf("mytype mykey")), verifyKeys = false)
        // check idem-potence
        val res2 = prov.addKnownHost(KnownHost("myserver.org", 2222, listOf("mytype mykey")), verifyKeys = false)

        // then
        assertTrue(res1.success)
        assertTrue(res2.success)
        val expectedContent = "[myserver.org]:2222 mytype mykey"
        val actualContent = prov.fileContent(KNOWN_HOSTS_FILE)
        assertTrue(actualContent?.contains(expectedContent) == true, "$expectedContent\nis not contained in:\n$actualContent")
    }
}