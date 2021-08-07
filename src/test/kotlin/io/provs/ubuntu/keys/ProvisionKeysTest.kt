package io.provs.ubuntu.keys

import io.provs.Secret
import io.provs.test.defaultTestContainer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS

internal class ProvisionKeysTest {

    @Test
    @EnabledOnOs(OS.LINUX)
    fun provisionKeysCurrentUser() {
        // given
        val a = defaultTestContainer()

        // when
        val res = a.provisionKeysCurrentUser(
            KeyPair(Secret(publicGPGSnakeoilKey()), Secret(privateGPGSnakeoilKey())),
            KeyPair(Secret(publicSSHSnakeoilKey()), Secret(privateSSHSnakeoilKey()))
        )

        // then
        assert(res.success)
    }
}

