package org.domaindrivenarchitecture.provs.desktop.infrastructure

import com.charleskorn.kaml.InvalidPropertyValueException
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.SecretSourceType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.FileNotFoundException

internal class K3SDesktopConfigRepositoryKtTest {

    @Test
    fun getConfig_successful() {
        // when
        val config = getConfig("src/test/resources/test-desktop-config.yaml")

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
            getConfig("src/test/resources/invalid-desktop-config.yaml")
        }
        assertEquals("Value for 'sourceType' is invalid: Value 'xxx' is not a valid option, permitted choices are: ENV, FILE, GOPASS, PASS, PLAIN, PROMPT", exception.message)
    }

    @Test
    fun getConfig_fails_due_to_missing_file() {
        val exception = assertThrows<FileNotFoundException> {
            getConfig("src/test/resources/Idonotexist.yaml")
        }
        assertEquals(FileNotFoundException::class.java, exception.javaClass)
    }
}