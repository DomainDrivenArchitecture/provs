package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.checkPackageInstalled
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertTrue

internal class FirefoxKtTest {

    @ExtensiveContainerTest
    fun installFirefox() {
        // when
        val result = defaultTestContainer().session {
            installFirefox()
            checkPackageInstalled("firefox")
        }

        // then
        assertTrue(result.success)
    }
}
