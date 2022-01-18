package org.domaindrivenarchitecture.provs.workplace.application

import ch.qos.logback.classic.Level
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.domaindrivenarchitecture.provs.framework.core.*
import org.domaindrivenarchitecture.provs.framework.core.cli.retrievePassword
import org.domaindrivenarchitecture.provs.framework.core.processors.PrintOnlyProcessor
import org.domaindrivenarchitecture.provs.framework.core.*
import org.domaindrivenarchitecture.provs.test.setRootLoggingLevel
import org.domaindrivenarchitecture.provs.workplace.domain.WorkplaceConfig
import org.domaindrivenarchitecture.provs.workplace.domain.WorkplaceType
import org.domaindrivenarchitecture.provs.workplace.domain.provisionWorkplace
import org.domaindrivenarchitecture.provs.workplace.infrastructure.getConfig
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

internal class CliWorkplaceKtTest {

    companion object {

        val printOnlyProv = Prov.newInstance(PrintOnlyProcessor())
        val testConfig = WorkplaceConfig(WorkplaceType.MINIMAL, gitUserName = "gittestuser", gitEmail = "git@test.mail")

        @BeforeAll
        @JvmStatic
        internal fun beforeAll() {
            mockkStatic(::local)
            every { local() } returns printOnlyProv

            mockkStatic(::remote)
            every { remote(any(), any(), any(), any()) } returns printOnlyProv

            mockkStatic(::getConfig)
            every { getConfig("testconfig.yaml") } returns testConfig

            mockkStatic(Prov::provisionWorkplace)
            every { any<Prov>().provisionWorkplace(any(), any(), any(), any(), any()) } returns ProvResult(
                true,
                cmd = "mocked command"
            )

            mockkStatic(::retrievePassword)
            every { retrievePassword(any()) } returns Secret("sec")
        }

        @AfterAll
        @JvmStatic
        internal fun afterAll() {
            unmockkStatic(::local)
            unmockkStatic(::remote)
            unmockkStatic(::getConfig)
            unmockkStatic(Prov::provisionWorkplace)
            unmockkStatic(::retrievePassword)
        }
    }

    @Test
    fun provision_workplace_locally() {
        // when
        main(arrayOf("-l", "testconfig.yaml"))

        // then
        verify {
            any<Prov>().provisionWorkplace(
                WorkplaceType.MINIMAL,
                null,
                null,
                testConfig.gitUserName,
                testConfig.gitEmail
            )
        }
    }

    @Test
    fun provision_workplace_remotely() {

        // when
        main(arrayOf("-i", "-r", "host123", "-u", "user123", "testconfig.yaml"))

        // then
        verify { remote("host123", "user123", Secret("sec"), any()) }
        verify {
            any<Prov>().provisionWorkplace(
                WorkplaceType.MINIMAL,
                null,
                null,
                testConfig.gitUserName,
                testConfig.gitEmail
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

        val expectedOutput = "Error: File\u001B[31m idontexist.yaml \u001B[0m was not found.Pls copy file \u001B[31m WorkplaceConfigExample.yaml \u001B[0m to file \u001B[31m idontexist.yaml \u001B[0m and change the content according to your needs."
        assertEquals(expectedOutput, outContent.toString().replace("\r", "").replace("\n", ""))

        verify(exactly = 0) { any<Prov>().provisionWorkplace(any()) }
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

        val expectedOutput = "Error: File \"src/test/resources/InvalidWorkplaceConfig.yaml\" has an invalid format and or invalid data."
        assertEquals(expectedOutput, outContent.toString().replace("\r", "").replace("\n", ""))

        verify(exactly = 0) { any<Prov>().provisionWorkplace(any()) }
    }
}