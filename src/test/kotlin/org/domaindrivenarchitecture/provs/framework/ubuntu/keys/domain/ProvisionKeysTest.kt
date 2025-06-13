package org.domaindrivenarchitecture.provs.framework.ubuntu.keys.domain

import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.privateGPGSnakeoilKey
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.privateSSHRSASnakeoilKey
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.publicGPGSnakeoilKey
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.publicSSHRSASnakeoilKey
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.test.tags.NonCi
import org.junit.jupiter.api.Assertions

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
        Assertions.assertTrue(res.success)
    }
}