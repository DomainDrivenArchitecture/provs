package org.domaindrivenarchitecture.provs.framework.ubuntu.keys

import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.NonCi
import org.junit.jupiter.api.Test

internal class ProvisionKeysTest {

    @Test
    @NonCi
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

