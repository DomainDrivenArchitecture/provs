package org.domaindrivenarchitecture.provs.configuration.domain

import io.mockk.*
import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.core.cli.createProvInstance
import org.domaindrivenarchitecture.provs.framework.core.cli.getPasswordToConfigureSudoWithoutPassword
import org.domaindrivenarchitecture.provs.framework.core.local
import org.domaindrivenarchitecture.provs.framework.core.processors.PrintOnlyProcessor
import org.domaindrivenarchitecture.provs.framework.core.remote
import org.domaindrivenarchitecture.provs.test.tags.NonCi
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class TargetCliCommandKtTest {

    companion object {
        @BeforeAll
        @JvmStatic
        internal fun beforeAll() {
            mockkObject(Prov)
            mockkStatic(::local)
            mockkStatic(::remote)
            every { remote(any(), any(), any(), any()) } returns Prov.newInstance(PrintOnlyProcessor())

            mockkStatic(::getPasswordToConfigureSudoWithoutPassword)
            every { getPasswordToConfigureSudoWithoutPassword() } returns Secret("sec")
        }

        @AfterAll
        @JvmStatic
        internal fun afterAll() {
            // cleanup
            unmockkAll()
        }
    }


    @Test
    @NonCi
    fun createProvInstance_local() {
        // given
        val cliCommand = TargetCliCommand("local", false)

        // when
        createProvInstance(cliCommand)

        // then
        verify { local() }
    }

    @Test
    fun createProvInstance_remote_with_sshKey() {
        // given
        val cliCommand = TargetCliCommand("user123@host123", false)

        // when
        createProvInstance(cliCommand)

        // then
        verify { remote("host123", "user123", null, any()) }
    }

    @Test
    fun createProvInstance_remote_with_interactive_password_retrieval() {
        // given
        val cliCommand = TargetCliCommand("user123:sec@host123", false)

        // when
        createProvInstance(cliCommand)

        // then
        verify { remote("host123", "user123", Secret("sec"), any()) }
    }
}