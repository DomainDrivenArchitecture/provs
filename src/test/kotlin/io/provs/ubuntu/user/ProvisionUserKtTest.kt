package io.provs.ubuntu.user

import io.provs.test.defaultTestContainer
import io.provs.ubuntu.keys.*
import io.provs.ubuntu.secret.SecretSourceType
import io.provs.ubuntu.user.base.configureUser
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