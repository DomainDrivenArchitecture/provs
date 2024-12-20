package org.domaindrivenarchitecture.provs.desktop.application

import ch.qos.logback.classic.Level
import io.mockk.*
import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.configuration.domain.TargetCliCommand
import org.domaindrivenarchitecture.provs.desktop.domain.DesktopCliCommand
import org.domaindrivenarchitecture.provs.desktop.domain.DesktopConfig
import org.domaindrivenarchitecture.provs.desktop.domain.DesktopType
import org.domaindrivenarchitecture.provs.desktop.domain.provisionDesktop
import org.domaindrivenarchitecture.provs.desktop.infrastructure.getConfig
import org.domaindrivenarchitecture.provs.framework.core.*
import org.domaindrivenarchitecture.provs.framework.core.cli.getPasswordToConfigureSudoWithoutPassword
import org.domaindrivenarchitecture.provs.framework.core.cli.quit
import org.domaindrivenarchitecture.provs.framework.core.processors.DummyProcessor
import org.domaindrivenarchitecture.provs.test.setRootLoggingLevel
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.ByteArrayOutputStream
import java.io.PrintStream

internal class ApplicationKtTest {

    companion object {

        val testConfig = DesktopConfig(gitUserName = "gittestuser", gitEmail = "git@test.mail")
        val cmd = DesktopCliCommand(
            DesktopType.BASIC,
            TargetCliCommand("user@host", false),
            ConfigFileName("bla")
        )

        @Suppress("unused") // false positive
        @BeforeAll
        @JvmStatic
        internal fun beforeAll() {
            val dummyProv = Prov.newInstance(DummyProcessor())

            mockkObject(Prov)
            every { Prov.newInstance(any(), any(), any(), any()) } returns dummyProv

            mockkStatic(::local)
            every { local() } returns dummyProv

            mockkStatic(::remote)
            every { remote(any(), any(), any(), any()) } returns dummyProv

            mockkStatic(::getConfig)
            every { getConfig("testconfig.yaml") } returns testConfig

            mockkStatic(Prov::provisionDesktop)
            every { any<Prov>().provisionDesktop(any(), any(), any(), any(), any()) } returns ProvResult(
                true,
                cmd = "mocked command"
            )

            mockkStatic(::getPasswordToConfigureSudoWithoutPassword)
            every { getPasswordToConfigureSudoWithoutPassword() } returns Secret("sec")
        }

        @Suppress("unused") // false positive
        @AfterAll
        @JvmStatic
        internal fun afterAll() {
            // cleanup
            unmockkAll()
        }
    }

    @Test
    fun provision_desktop_remotely() {

        // when
        main(arrayOf("basic", "user123:sec@host123.xyz", "-c", "testconfig.yaml"))

        // then
        verify { remote("host123.xyz", "user123", Secret("sec"), any()) }
        verify {
            any<Prov>().provisionDesktop(
                DesktopType.BASIC,
                null,
                null,
                testConfig.gitUserName,
                testConfig.gitEmail,
            )
        }
    }

    @Test
    fun prints_error_message_if_config_not_found() {
        mockkStatic(::quit)
        every { quit(any()) } throws RuntimeException("mockked")

        // given
        setRootLoggingLevel(Level.OFF)

        val outContent = ByteArrayOutputStream()
        val errContent = ByteArrayOutputStream()
        val originalOut = System.out
        val originalErr = System.err

        System.setOut(PrintStream(outContent))
        System.setErr(PrintStream(errContent))

        // when
        assertThrows<RuntimeException> {
            main(arrayOf("basic", "someuser@remotehost", "-c", "idontexist.yaml"))
        }

        // then
        System.setOut(originalOut)
        System.setErr(originalErr)

        val expectedOutput =
            "Error: File\u001B[31m idontexist.yaml \u001B[0m was not found.Pls copy file \u001B[31m desktop-config-example.yaml \u001B[0m to file \u001B[31m idontexist.yaml \u001B[0m and change the content according to your needs.No suitable config found."
        assertTrue(
            outContent.toString().replace("\r", "").replace("\n", "").contains(expectedOutput),
            "$expectedOutput\nnot found in:\n$outContent"
        )

        verify(exactly = 0) { any<Prov>().provisionDesktop(any(), any(), any(), any(), any()) }

        unmockkStatic(::quit)
    }

    @Test
    fun prints_error_message_if_config_not_parsable() {
        mockkStatic(::quit)
        every { quit(any()) } throws RuntimeException("mockked")

        // given
        setRootLoggingLevel(Level.OFF)

        val outContent = ByteArrayOutputStream()
        val errContent = ByteArrayOutputStream()
        val originalOut = System.out
        val originalErr = System.err

        System.setOut(PrintStream(outContent))
        System.setErr(PrintStream(errContent))

        // when
        assertThrows<RuntimeException> {
            main(arrayOf("basic", "someuser@remotehost", "-c", "src/test/resources/invalid-desktop-config.yaml"))
        }

        // then
        System.setOut(originalOut)
        System.setErr(originalErr)

        val expectedOutput =
            "Error: File \"src/test/resources/invalid-desktop-config.yaml\" has an invalid format and or invalid data.No suitable config found."
        assertTrue(
            outContent.toString().replace("\r", "").replace("\n", "").contains(expectedOutput),
            "$expectedOutput\nnot found in:\n$outContent"
        )

        verify(exactly = 0) { any<Prov>().provisionDesktop(any(), any(), any(), any(), any()) }

        unmockkStatic(::quit)
    }
}