package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.fileExists
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.assertTrue

internal class BinariesC4kKtTest {

    @ContainerTest
    fun downloadC4kBinaries() {
        // when
        val res = defaultTestContainer().installBinariesC4k()

        // then
        assertTrue(defaultTestContainer().fileExists("/usr/local/bin/c4k-nextcloud-standalone.jar", sudo = true))
        assertTrue(defaultTestContainer().fileExists("/usr/local/bin/c4k-jira-standalone.jar", sudo = true))
        assertTrue(defaultTestContainer().fileExists("/usr/local/bin/c4k-keycloak-standalone.jar", sudo = true))
        assertTrue(defaultTestContainer().fileExists("/usr/local/bin/c4k-mastodon-bot-standalone.jar", sudo = true))
        assertTrue(defaultTestContainer().fileExists("/usr/local/bin/c4k-shynet-standalone.jar", sudo = true))
        assertTrue(res.success)
    }
}