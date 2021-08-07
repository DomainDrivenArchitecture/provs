package org.domaindrivenarchitecture.provs.ubuntu.user

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.ubuntu.keys.*
import org.domaindrivenarchitecture.provs.ubuntu.secret.SecretSourceType
import org.domaindrivenarchitecture.provs.ubuntu.user.base.configureUser
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS


internal class ProvisionUserKtTest {

    @Test
    @EnabledOnOs(OS.LINUX)
    fun configureUser() {
        // given
        val a = defaultTestContainer()

        // when
        val res = a.configureUser(
            UserConfig(
                "testuser",
                "test@mail.com",
                KeyPairSource(SecretSourceType.PLAIN, publicGPGSnakeoilKey(), privateGPGSnakeoilKey()),
                KeyPairSource(SecretSourceType.PLAIN, publicSSHSnakeoilKey(), privateSSHSnakeoilKey())
            )
        )

        // then
        assert(res.success)
    }
}