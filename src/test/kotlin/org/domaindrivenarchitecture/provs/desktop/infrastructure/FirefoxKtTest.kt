package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.isPackageInstalled
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class FirefoxKtTest {

    @Test
    @ExtensiveContainerTest
    fun installFirefox() {
        // when
        val res = defaultTestContainer().installFirefox()

        // then
        assertTrue(res.success)

        val ffIsInstalled = defaultTestContainer().isPackageInstalled("firefox")
        assertTrue(ffIsInstalled)
    }
}
