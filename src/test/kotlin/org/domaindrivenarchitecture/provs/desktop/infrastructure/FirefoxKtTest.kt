package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.remote
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.deleteFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.fileContainsText
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.infrastructure.checkPackage
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.domain.secretSources.PromptSecretSource
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class FirefoxKtTest {

    // Attention: this test does not test full functionality of installPpaFirefox, e.g. does not test
    // remove snap-firefox, as this test runs against a container, which does have neither snap nor snap-firefox installed
    @ExtensiveContainerTest
    fun installFirefox() {
        // given
        val prov = defaultTestContainer()

        // when
        val result = prov.session {
            deleteFile("/etc/apt/apt.conf.d/51unattended-upgrades-firefox", sudo = true)
            deleteFile("/etc/apt/preferences.d/mozillateam", sudo = true)
            installPpaFirefox()
        }
        val result2 = prov.installPpaFirefox()

        // then
        assertTrue(result.success)
        assertEquals("Firefox already installed with ppa", result2.out)

        assertTrue(prov.checkPackage("firefox"))
        assertTrue(
            prov.fileContainsText(
                "/etc/apt/apt.conf.d/51unattended-upgrades-firefox",
                "Unattended-Upgrade::Allowed-Origins:: \"LP-PPA-mozillateam:\${distro_codename}\";\n",
                sudo = true
            )
        )
        val expectedPolicyLine = Regex("1001? https?://ppa.launchpad(?:content)?.net/mozillateam/ppa/ubuntu")
        val policy = prov.cmd("apt policy firefox").out
        assertTrue(
            policy?.contains(expectedPolicyLine) ?: false,
            "$expectedPolicyLine was not found in $policy"
        )
    }

    /**
     * Tests installing firefox on a remote machine, e.g. a virtual machine
     */
    @Test
    @Disabled("Update connection details, then enable the test and run manually")
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
            installPpaFirefox()
            firefoxVersion = cmd("apt list firefox --installed").out ?: ""
            if (checkPackage("firefox")) {
                addResult(true, "Firefox installed")
            } else {
                addResult(false, "Firefox not installed")
            }
        }

        // then
        assertTrue(result.success)
        println("Firefox: $firefoxVersion")
        assertTrue(firefoxVersion.contains("build") && !firefoxVersion.contains("snap"))
    }
}
