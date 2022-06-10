package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MsTeamsTest {

    @ContainerTest
    fun installMsTeams() {
        // given
        val a = defaultTestContainer()
        // when
        val res = a.task { installMsTeams() }
        // then
        assertTrue(res.success)
    }
}