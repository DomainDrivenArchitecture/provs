package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertTrue


internal class NextcloudClientTest {
    @ExtensiveContainerTest
    fun test_installNextcloudClient() {
        //given
        val prov = defaultTestContainer()
        prov.cmd("DEBIAN_FRONTEND=noninteractive TZ=${"Europe/Berlin"} apt-get -q=2 install tzdata", sudo = true)

        // when
        val res = prov.task {
            installNextcloudClient()
            installNextcloudClient()
        }

        // then
        assertTrue(res.success)
    }
}
