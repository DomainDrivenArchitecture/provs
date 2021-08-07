package io.provs.ubuntu.keys.base

import io.provs.Secret
import io.provs.test.defaultTestContainer
import io.provs.ubuntu.keys.*
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