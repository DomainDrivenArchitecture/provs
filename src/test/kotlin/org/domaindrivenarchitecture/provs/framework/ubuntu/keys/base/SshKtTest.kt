package org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base

import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.fileContent
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.*
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

internal class SshKtTest {

    @ContainerTest
    fun configureSshKeys_for_ssh_type_rsa() {
        // given
        val prov = defaultTestContainer()

        // when
        val res = prov.configureSshKeys(SshKeyPair(Secret(publicSSHRSASnakeoilKey()), Secret(privateSSHRSASnakeoilKey())))

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
        val res = prov.configureSshKeys(SshKeyPair(Secret(publicED25519SnakeOilKey()), Secret(privateED25519SnakeOilKey())))

        // then
        assertTrue(res.success)

        val publicSshKeyFileContent = prov.fileContent("~/.ssh/id_ed25519.pub")
        assertEquals(publicED25519SnakeOilKey() + "\n", publicSshKeyFileContent)

        val privateSshKeyFileContent = prov.fileContent("~/.ssh/id_ed25519")
        assertEquals(privateED25519SnakeOilKey() + "\n", privateSshKeyFileContent)
    }
}