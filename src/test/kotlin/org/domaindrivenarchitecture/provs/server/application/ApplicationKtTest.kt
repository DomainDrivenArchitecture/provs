package org.domaindrivenarchitecture.provs.server.application

import io.mockk.*
import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.core.local
import org.domaindrivenarchitecture.provs.framework.core.processors.DummyProcessor
import org.domaindrivenarchitecture.provs.framework.core.remote
import org.domaindrivenarchitecture.provs.framework.ubuntu.scheduledjobs.domain.scheduleMonthlyReboot
import org.domaindrivenarchitecture.provs.server.domain.k3s.provisionK3s
import org.junit.jupiter.api.Test


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

        mockkStatic(Prov::scheduleMonthlyReboot)
        every { any<Prov>().scheduleMonthlyReboot() } returns ProvResult(
            true,
            cmd = "mocked command"
        )

        mockkStatic(Prov::provisionK3s)
        every { any<Prov>().provisionK3s(any(), any(), any(), any()) } returns ProvResult(
            true,
            cmd = "mocked command"
        )

        // when
        main(arrayOf("k3s", "user123:pw@host123.meissa", "-o", "monthly_reboot"))

        //then
        verify(exactly = 1) { any<Prov>().scheduleMonthlyReboot() }
        verify(exactly = 0) { any<Prov>().provisionK3s(any(), any(), any(), any()) }

        // cleanup
        unmockkAll()
    }

}