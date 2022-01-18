package org.domaindrivenarchitecture.provs.core.cli

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.core.local
import org.domaindrivenarchitecture.provs.framework.core.processors.PrintOnlyProcessor
import org.domaindrivenarchitecture.provs.framework.core.cli.TargetCliCommand
import org.domaindrivenarchitecture.provs.framework.core.cli.createProvInstance
import org.domaindrivenarchitecture.provs.framework.core.cli.retrievePassword
import org.domaindrivenarchitecture.provs.framework.core.remote
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class CliTargetCommandKtTest {

    companion object {
        @BeforeAll
        @JvmStatic
        internal fun beforeAll() {
            mockkStatic(::local)
            mockkStatic(::remote)
            every { remote(any(), any(), any(), any()) } returns Prov.newInstance(PrintOnlyProcessor())

            mockkStatic(::retrievePassword)
            every { retrievePassword(any()) } returns Secret("sec")
        }

        @AfterAll
        @JvmStatic
        internal fun afterAll() {
            unmockkStatic(::local)
            unmockkStatic(::remote)
            unmockkStatic(::retrievePassword)
        }
    }


    @Test
    fun createProvInstance_local() {
        // given
        val cliCommand = TargetCliCommand(true, null, null, false, null, false)

        // when
        createProvInstance(cliCommand)

        // then
        verify { local() }
    }

    @Test
    fun createProvInstance_remote_with_sshKey() {
        // given
        val cliCommand = TargetCliCommand(false, "host123", "user123", false, null, true)

        // when
        createProvInstance(cliCommand)

        // then
        verify { remote("host123", "user123", null, any()) }
    }

    @Test
    fun createProvInstance_remote_with_interactive_password_retrieval() {
        // given
        val cliCommand = TargetCliCommand(false, "host123", "user123", true, null, false)

        // when
        createProvInstance(cliCommand)

        // then
        verify { remote("host123", "user123", Secret("sec"), any()) }
    }
}