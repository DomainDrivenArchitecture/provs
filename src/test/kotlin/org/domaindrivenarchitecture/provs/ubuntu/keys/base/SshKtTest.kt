package org.domaindrivenarchitecture.provs.ubuntu.keys.base

import org.domaindrivenarchitecture.provs.core.Secret
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.ubuntu.keys.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class SshKtTest {

    @Test
    fun configureSshKeys() {
        // given
        val a = defaultTestContainer()

        // when
        val res = a.configureSshKeys(KeyPair(Secret(publicSSHSnakeoilKey()), Secret(privateSSHSnakeoilKey())))

        // then
        assertTrue(res.success)

    }
}