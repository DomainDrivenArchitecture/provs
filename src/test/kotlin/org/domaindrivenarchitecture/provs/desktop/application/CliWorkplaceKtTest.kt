package org.domaindrivenarchitecture.provs.desktop.application

import ch.qos.logback.classic.Level
import io.mockk.*
import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.configuration.domain.TargetCliCommand
import org.domaindrivenarchitecture.provs.desktop.domain.DesktopConfig
import org.domaindrivenarchitecture.provs.desktop.domain.WorkplaceType
import org.domaindrivenarchitecture.provs.desktop.domain.provisionWorkplace
import org.domaindrivenarchitecture.provs.desktop.infrastructure.getConfig
import org.domaindrivenarchitecture.provs.framework.core.*
import org.domaindrivenarchitecture.provs.framework.core.cli.retrievePassword
import org.domaindrivenarchitecture.provs.framework.core.processors.PrintOnlyProcessor
import org.domaindrivenarchitecture.provs.test.setRootLoggingLevel
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

internal class CliWorkplaceKtTest {

    companion object {

        val testConfig = DesktopConfig(WorkplaceType.MINIMAL, gitUserName = "gittestuser", gitEmail = "git@test.mail")
        val cmd = DesktopCliCommand(
            ConfigFileName("bla"),
            listOf(),
            TargetCliCommand(null, null, null, false, null, false)
        )

        @BeforeAll
        @JvmStatic
        internal fun beforeAll() {
            val printOnlyProv = Prov.newInstance(PrintOnlyProcessor())

            mockkObject(Prov)
            every { Prov.newInstance(any(), any(), any(), any(), ) } returns printOnlyProv

            mockkStatic(::local)
            every { local() } returns printOnlyProv

            mockkStatic(::remote)
            every { remote(any(), any(), any(), any()) } returns printOnlyProv

            mockkStatic(::getConfig)
            every { getConfig("testconfig.yaml") } returns testConfig

            mockkStatic(Prov::provisionWorkplace)
            every { any<Prov>().provisionWorkplace(any(), any(), any(), any(), any(), any()) } returns ProvResult(
                true,
                cmd = "mocked command"
            )

            mockkStatic(::retrievePassword)
            every { retrievePassword(any()) } returns Secret("sec")
        }

        @AfterAll
        @JvmStatic
        internal fun afterAll() {
            unmockkObject(Prov)
            unmockkStatic(::local)
            unmockkStatic(::remote)
            unmockkStatic(::getConfig)
            unmockkStatic(Prov::provisionWorkplace)
            unmockkStatic(::retrievePassword)
        }
    }

    @Test
    fun provision_workplace_remotely() {

        // when
        main(arrayOf("-i", "-r", "host123.xyz", "-u", "user123", "testconfig.yaml"))

        // then
        verify { remote("host123.xyz", "user123", Secret("sec"), any()) }
        verify {
            any<Prov>().provisionWorkplace(
                WorkplaceType.MINIMAL,
                null,
                null,
                testConfig.gitUserName,
                testConfig.gitEmail,
                any()   // todo should be: cmd , but needs to be fixed
            )
        }
    }

    @Test
    fun prints_error_message_if_config_not_found() {
        // given
        setRootLoggingLevel(Level.OFF)

        val outContent = ByteArrayOutputStream()
        val errContent = ByteArrayOutputStream()
        val originalOut = System.out
        val originalErr = System.err

        System.setOut(PrintStream(outContent))
        System.setErr(PrintStream(errContent))

        // when
        main(arrayOf("-l", "idontexist.yaml"))

        // then
        System.setOut(originalOut)
        System.setErr(originalErr)

        val expectedOutput =
            "Error: File\u001B[31m ConfigFileName(fileName=idontexist.yaml) \u001B[0m was not found.Pls copy file \u001B[31m WorkplaceConfigExample.yaml \u001B[0m to file \u001B[31m ConfigFileName(fileName=idontexist.yaml) \u001B[0m and change the content according to your needs."
        assertEquals(expectedOutput, outContent.toString().replace("\r", "").replace("\n", ""))

        verify(exactly = 0) { any<Prov>().provisionWorkplace(any(), cmd = cmd) }
    }

    @Test
    fun prints_error_message_if_config_not_parsable() {
        // given
        setRootLoggingLevel(Level.OFF)

        val outContent = ByteArrayOutputStream()
        val errContent = ByteArrayOutputStream()
        val originalOut = System.out
        val originalErr = System.err

        System.setOut(PrintStream(outContent))
        System.setErr(PrintStream(errContent))

        // when
        main(arrayOf("-l", "src/test/resources/InvalidWorkplaceConfig.yaml"))

        // then
        System.setOut(originalOut)
        System.setErr(originalErr)

        val expectedOutput =
            "Error: File \"ConfigFileName(fileName=src/test/resources/InvalidWorkplaceConfig.yaml)\" has an invalid format and or invalid data."
        assertEquals(expectedOutput, outContent.toString().replace("\r", "").replace("\n", ""))

        verify(exactly = 0) { any<Prov>().provisionWorkplace(any(), cmd = cmd) }
    }
}