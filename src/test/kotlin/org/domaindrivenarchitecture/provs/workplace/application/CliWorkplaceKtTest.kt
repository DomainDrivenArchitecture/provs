package org.domaindrivenarchitecture.provs.workplace.application

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.domaindrivenarchitecture.provs.core.*
import org.domaindrivenarchitecture.provs.core.cli.retrievePassword
import org.domaindrivenarchitecture.provs.core.processors.PrintOnlyProcessor
import org.domaindrivenarchitecture.provs.workplace.domain.WorkplaceConfig
import org.domaindrivenarchitecture.provs.workplace.domain.WorkplaceType
import org.domaindrivenarchitecture.provs.workplace.domain.provisionWorkplace
import org.domaindrivenarchitecture.provs.workplace.infrastructure.getConfig
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

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
            every { any<Prov>().provisionWorkplace(any(), any(), any(), any(), any()) } returns ProvResult(true, cmd = "mocked command")

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
        verify { any<Prov>().provisionWorkplace(WorkplaceType.MINIMAL, null, null, testConfig.gitUserName, testConfig.gitEmail) }
    }

    @Test
    fun provision_workplace_remotely() {

        // when
        main(arrayOf("-i", "-r", "host123", "-u", "user123", "testconfig.yaml"))

        // then
        verify { remote("host123", "user123", Secret("sec"), any()) }
        verify { any<Prov>().provisionWorkplace(WorkplaceType.MINIMAL, null, null, testConfig.gitUserName, testConfig.gitEmail) }
    }
}