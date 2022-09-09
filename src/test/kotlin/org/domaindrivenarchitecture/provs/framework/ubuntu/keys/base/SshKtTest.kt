package org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base

import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.*
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.assertTrue

internal class SshRSAKtTest {

    @ContainerTest
    fun configureSshKeys() {
        // given
        val a = defaultTestContainer()

        // when
        val res = a.configureSshKeys(SshKeyPair(Secret(publicSSHRSASnakeoilKey()), Secret(privateSSHRSASnakeoilKey())))

        // then
        assertTrue(res.success)

    }
}

internal class SshED25519KtTest {

    @ContainerTest
    fun configureSshKeys() {
        // given
        val a = defaultTestContainer()

        // when
        val res = a.configureSshKeys(SshKeyPair(Secret(publicED25519SnakeOilKey()), Secret(privateED25519SnakeOilKey())))

        // then
        assertTrue(res.success)

    }
}