package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled

internal class NextcloudClientTest {
    @Disabled // test is hanging sometimes
    @ExtensiveContainerTest
    fun test_installNextcloudClient() {
        // when
        val res = defaultTestContainer().installNextcloudClient()

        // then
        assertTrue(res.success)
    }
}
