package org.domaindrivenarchitecture.provs.framework.ubuntu.keys

import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.test.tags.NonCi
import org.junit.jupiter.api.Assertions.assertTrue

internal class ProvisionKeysTest {

    @ContainerTest
    @NonCi
    fun provisionKeys() {
        // given
        val a = defaultTestContainer()

        // when
        val res = a.provisionKeys(
            KeyPair(Secret(publicGPGSnakeoilKey()), Secret(privateGPGSnakeoilKey())),
            SshKeyPair(Secret(publicSSHRSASnakeoilKey()), Secret(privateSSHRSASnakeoilKey()))
        )

        // then
        assertTrue(res.success)
    }
}

