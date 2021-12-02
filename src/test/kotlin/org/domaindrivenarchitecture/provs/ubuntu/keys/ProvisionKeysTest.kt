package org.domaindrivenarchitecture.provs.ubuntu.keys

import org.domaindrivenarchitecture.provs.core.Secret
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Test

internal class ProvisionKeysTest {

    @Test
    @ContainerTest
    fun provisionKeys() {
        // given
        val a = defaultTestContainer()

        // when
        val res = a.provisionKeys(
            KeyPair(Secret(publicGPGSnakeoilKey()), Secret(privateGPGSnakeoilKey())),
            KeyPair(Secret(publicSSHSnakeoilKey()), Secret(privateSSHSnakeoilKey()))
        )

        // then
        assert(res.success)
    }
}

