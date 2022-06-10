package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertTrue

class MsTeamsKtTest {

    @ExtensiveContainerTest
    fun installMsTeams() {
        // given
        val a = defaultTestContainer()
        // when
        val res = a.task { installMsTeams() }
        // then
        assertTrue(res.success)
    }
}