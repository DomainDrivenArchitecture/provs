package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.remote
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.checkPackageInstalled
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.secretSources.PromptSecretSource
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class FirefoxKtTest {

    // Attention: this test does not test full functionality of installFirefox, e.g. does not test
    // remove snap, as this test runs against a container which does not have snap-firefox installed
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

    @Test
    @Disabled("Update connection details,then enable and run manually")
    fun installFirefox_remotely() {
        val host = "192.168.56.123"
        val user = "user"
        var firefoxVersion = ""

        // when
        val result = remote(
            host,
            user,
            /* remove for ssh authentication */
            PromptSecretSource("Remote password for user $user").secret()
        ).session {
            installFirefox()
            firefoxVersion = cmd("apt list firefox --installed").out ?: ""
            checkPackageInstalled("firefox")
        }

        // then
        assertTrue(result.success)
        println("Firefox: $firefoxVersion")
        assertTrue(firefoxVersion.contains("ubuntu") && !firefoxVersion.contains("snap"))
    }
}
