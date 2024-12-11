package org.domaindrivenarchitecture.provs.server.application

import io.mockk.*
import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.core.local
import org.domaindrivenarchitecture.provs.framework.core.processors.DummyProcessor
import org.domaindrivenarchitecture.provs.framework.core.remote
import org.domaindrivenarchitecture.provs.framework.ubuntu.scheduledjobs.domain.scheduleMonthlyReboot
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.SecretSourceType
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.SecretSupplier
import org.domaindrivenarchitecture.provs.server.domain.hetzner_csi.HetznerCSIConfig
import org.domaindrivenarchitecture.provs.server.domain.hetzner_csi.provisionHetznerCSI
import org.domaindrivenarchitecture.provs.server.domain.k3s.provisionK3s
import org.domaindrivenarchitecture.provs.server.infrastructure.findHetznerCSIConfig
import org.junit.jupiter.api.Test


val dummyResult = ProvResult(true, cmd = "mocked command")


class ApplicationKtTest {

    @Test
    fun test_main_with_only_monthly_reboot() {
        // given
        val dummyProv = Prov.newInstance(DummyProcessor())

        mockkObject(Prov)
        every { Prov.newInstance(any(), any(), any(), any()) } returns dummyProv

        mockkStatic(::local)
        every { local() } returns dummyProv

        mockkStatic(::remote)
        every { remote(any(), any(), any(), any()) } returns dummyProv

        mockkStatic(Prov::provisionHetznerCSI)
        every { any<Prov>().provisionHetznerCSI(any()) } returns dummyResult

        mockkStatic(Prov::scheduleMonthlyReboot)
        every { any<Prov>().scheduleMonthlyReboot() } returns dummyResult

        mockkStatic(Prov::provisionK3s)
        every { any<Prov>().provisionK3s(any(), any(), any(), any()) } returns dummyResult

        // when
        main(arrayOf("k3s", "user123:pw@host123.meissa", "-o", "monthly_reboot"))

        //then
        verify(exactly = 1) { any<Prov>().scheduleMonthlyReboot() }
        verify(exactly = 0) { any<Prov>().provisionHetznerCSI(any()) }
        verify(exactly = 0) { any<Prov>().provisionK3s(any(), any(), any(), any()) }

        // cleanup
        unmockkAll()
    }

    @Test
    fun test_main_with_only_hetzner_csi() {
        // given
        val dummyProv = Prov.newInstance(DummyProcessor())

        mockkObject(Prov)
        every { Prov.newInstance(any(), any(), any(), any()) } returns dummyProv

        mockkStatic(::local)
        every { local() } returns dummyProv

        mockkStatic(::remote)
        every { remote(any(), any(), any(), any()) } returns dummyProv

        mockkStatic(::findHetznerCSIConfig)
        every { findHetznerCSIConfig(any()) } returns HetznerCSIConfig(
            hcloudApiToken = SecretSupplier(SecretSourceType.PLAIN,"dummy"),
            encryptionPassphrase = SecretSupplier(SecretSourceType.PLAIN,"dummy"),
        )

        mockkStatic(Prov::provisionHetznerCSI)
        every { any<Prov>().provisionHetznerCSI(any()) } returns dummyResult

        mockkStatic(Prov::scheduleMonthlyReboot)
        every { any<Prov>().scheduleMonthlyReboot() } returns dummyResult

        mockkStatic(Prov::provisionK3s)
        every { any<Prov>().provisionK3s(any(), any(), any(), any()) } returns dummyResult

        // when
        main(arrayOf("k3s", "user123:pw@host123.meissa", "-o", "hetzner_csi"))

        //then
        verify(exactly = 1) { any<Prov>().provisionHetznerCSI(any()) }
        verify(exactly = 0) { any<Prov>().scheduleMonthlyReboot() }
        verify(exactly = 0) { any<Prov>().provisionK3s(any(), any(), any(), any()) }

        // cleanup
        unmockkAll()
    }
}