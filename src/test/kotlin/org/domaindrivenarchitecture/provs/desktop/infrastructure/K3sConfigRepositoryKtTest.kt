package org.domaindrivenarchitecture.provs.desktop.infrastructure

import com.charleskorn.kaml.InvalidPropertyValueException
import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.SecretSourceType
import org.domaindrivenarchitecture.provs.server.infrastructure.k3s.getK3sConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.FileNotFoundException

internal class K3sConfigRepositoryKtTest {

    @Test
    fun getConfig_successful() {
        // when
        val config = getConfig("src/test/resources/TestWorkplaceConfig.yaml")

        // then
        assertEquals("username", config.gitUserName)
        assertEquals("for@git.email", config.gitEmail)

        assertEquals(SecretSourceType.FILE, config.ssh?.sourceType)
        assertEquals("~/.ssh/id_rsa.pub", config.ssh?.publicKey)
        assertEquals("~/.ssh/id_rsa", config.ssh?.privateKey)

        assertEquals(SecretSourceType.GOPASS, config.gpg?.sourceType)
        assertEquals("path/to/pub.key", config.gpg?.publicKey)
        assertEquals("path/to/priv.key", config.gpg?.privateKey)
    }

    @Test
    fun getConfig_fails_due_to_invalidProperty() {
        val exception = assertThrows<InvalidPropertyValueException> {
            getConfig("src/test/resources/InvalidWorkplaceConfig.yaml")
        }
        assertEquals("Value for 'sourceType' is invalid: Value 'xxx' is not a valid option, permitted choices are: FILE, GOPASS, PASS, PLAIN, PROMPT", exception.message)
    }

    @Test
    fun getConfig_fails_due_to_missing_file() {
        val exception = assertThrows<FileNotFoundException> {
            getK3sConfig(ConfigFileName("src/test/resources/Idonotexist.yaml"))
        }
        assertEquals("src/test/resources/Idonotexist.yaml (No such file or directory)", exception.message)
    }
}