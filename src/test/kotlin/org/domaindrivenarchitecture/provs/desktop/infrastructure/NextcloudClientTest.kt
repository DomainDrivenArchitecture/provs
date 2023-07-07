package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertTrue

internal class NextcloudClientTest {
    @ExtensiveContainerTest
    fun test_installNextcloudClient() {
        // when
        val res = defaultTestContainer().installNextcloudClient()

        // then
        assertTrue(res.success)
    }
}
