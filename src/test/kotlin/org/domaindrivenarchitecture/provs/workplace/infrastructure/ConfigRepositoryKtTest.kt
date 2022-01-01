package org.domaindrivenarchitecture.provs.workplace.infrastructure

import com.charleskorn.kaml.InvalidPropertyValueException
import org.domaindrivenarchitecture.provs.ubuntu.secret.SecretSourceType
import org.domaindrivenarchitecture.provs.workplace.domain.WorkplaceType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.FileNotFoundException

internal class ConfigRepositoryKtTest {

    @Test
    fun getConfig_successful() {
        // when
        val config = getConfig("src/test/resources/TestWorkplaceConfig.yaml")

        // then
        assertEquals(WorkplaceType.OFFICE, config.type)
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
        assertThrows<InvalidPropertyValueException> {
            getConfig("src/test/resources/InvalidWorkplaceConfig.yaml")
        }

    }

    @Test
    fun getConfig_fails_due_to_non_existing_file() {
        assertThrows<FileNotFoundException> {
            getConfig("src/test/resources/Idonotexist.yaml")
        }

    }
}