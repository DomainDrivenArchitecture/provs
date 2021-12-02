//package org.domaindrivenarchitecture.provs.core.cli
//
//import io.mockk.every
//import io.mockk.mockkStatic
//import io.mockk.verify
//import org.domaindrivenarchitecture.provs.core.Prov
//import org.domaindrivenarchitecture.provs.core.Secret
//import org.domaindrivenarchitecture.provs.core.local
//import org.domaindrivenarchitecture.provs.core.processors.PrintOnlyProcessor
//import org.domaindrivenarchitecture.provs.core.remote
//import org.junit.jupiter.api.Test
//
//internal class CliCommandKtTest {
//
//    @Test
//    fun createProvInstance_local() {
//        mockkStatic(::local)
//
//        // given
//        val cliCommand = CliCommand(true, null, null, false, null, false)
//
//        // when
//        createProvInstance(cliCommand)
//
//        // then
//        verify { local() }
//    }
//
//    @Test
//    fun createProvInstance_remote_with_sshKey() {
//        mockkStatic(::remote)
//        every { remote(any(), any(), any(), any()) } returns Prov.newInstance(PrintOnlyProcessor())
//
//        // given
//        val cliCommand = CliCommand(false, "host123", "user123", false, null, true)
//
//        // when
//        createProvInstance(cliCommand)
//
//        // then
//        verify { remote("host123", "user123", null, any()) }
//    }
//
//    @Test
//    fun createProvInstance_remote_with_interactive_password_retrieval() {
//        mockkStatic(::remote)
//        every { remote(any(), any(), any(), any()) } returns Prov.newInstance(PrintOnlyProcessor())
//
//        mockkStatic(::retrievePassword)
//        every { retrievePassword(any()) } returns Secret("sec")
//
//        // given
//        val cliCommand = CliCommand(false, "host123", "user123", true, null, false)
//
//        // when
//        createProvInstance(cliCommand)
//
//        // then
//        verify { remote("host123", "user123", Secret("sec"), any()) }
//    }
//}