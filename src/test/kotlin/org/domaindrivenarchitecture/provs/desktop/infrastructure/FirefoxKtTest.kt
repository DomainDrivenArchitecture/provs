package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.isPackageInstalled
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertTrue

internal class FirefoxKtTest {

    @ExtensiveContainerTest
    fun installFirefox() {
        defaultTestContainer().session {
            // when
            val res = installFirefox()

            // then
            assertTrue(res.success)

            val ffIsInstalled = isPackageInstalled("firefox")
            assertTrue(ffIsInstalled)
        }
    }
}
