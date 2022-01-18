package org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base

import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.KeyPair
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.privateGPGSnakeoilKey
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.publicGPGSnakeoilKey
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class GpgKtTest {

    @Test
    @ContainerTest
    fun gpgFingerprint_returnsCorrectFingerprint() {
        // given
        val a = defaultTestContainer()
        a.aptInstall("gpg")
        a.cmd("gpg --version")   // just for info reasons

        // when
        val fingerprint = a.gpgFingerprint(publicGPGSnakeoilKey())

        // then
        assertEquals("85052C6954262D61D4E9977E0674104CA81A4905", fingerprint)
    }


    @Test
    @ContainerTest
    fun configureGpgKeys() {
        // given
        val a = defaultTestContainer()

        // when
        val res = a.configureGpgKeys(KeyPair(Secret(publicGPGSnakeoilKey()), Secret(privateGPGSnakeoilKey())))

        // then
        assertTrue(res.success)
    }


    @Test
    @ContainerTest
    fun configureGpgKeysTrusted() {
        // given
        val a = defaultTestContainer()

        // when
        val res = a.configureGpgKeys(KeyPair(Secret(publicGPGSnakeoilKey()), Secret(privateGPGSnakeoilKey())), true)

        // then
        assertTrue(res.success)
        val trustedKey = a.cmd("gpg -K | grep ultimate").out
        assertEquals("uid           [ultimate] snakeoil <snake@oil.com>", trustedKey?.trim())
    }


    @Test
    @ContainerTest
    fun configureGpgKeysIsIdempotent() {
        // given
        val a = defaultTestContainer()

        // when
        val res = a.configureGpgKeys(KeyPair(Secret(publicGPGSnakeoilKey()), Secret(privateGPGSnakeoilKey())))
        val res2 = a.configureGpgKeys(KeyPair(Secret(publicGPGSnakeoilKey()), Secret(privateGPGSnakeoilKey())))

        // then
        assertTrue(res.success)
        assertTrue(res2.success)
    }
}


