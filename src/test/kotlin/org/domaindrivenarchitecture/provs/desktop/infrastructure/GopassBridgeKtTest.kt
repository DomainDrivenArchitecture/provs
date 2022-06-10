package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.core.docker.exitAndRmContainer
import org.domaindrivenarchitecture.provs.framework.core.local
import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.KeyPair
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base.configureGpgKeys
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.domaindrivenarchitecture.provs.test.tags.NonCi
import org.domaindrivenarchitecture.provs.test_keys.privateGPGSnakeoilKey
import org.domaindrivenarchitecture.provs.test_keys.publicGPGSnakeoilKey
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test


internal class GopassBridgeKtTest {

    @ExtensiveContainerTest
    fun test_downloadGopassBridge() {
        // given
        local().exitAndRmContainer("provs_test")
        val a = defaultTestContainer()
        a.aptInstallCurl()

        // when
        val res = a.downloadGopassBridge()

        // then
        assertTrue(res.success)
    }

    @ExtensiveContainerTest
    @NonCi
    fun test_install_and_configure_GopassBridgeJsonApi() {
        // given
        local().exitAndRmContainer("provs_test")
        val a = defaultTestContainer()
        val preparationResult = a.task {
            aptInstallCurl()
            configureGpgKeys(
                KeyPair(Secret(publicGPGSnakeoilKey()), Secret(privateGPGSnakeoilKey())),
                trust = true,
                skipIfExistin = false
            )
            installGopass()
            if (!chk("gopass ls")) {
                // configure/init gopass in default location with gpg-key-fingerprint of snakeoil keys
                cmd("printf \"\\ntest\\ntest@test.org\\n\" | gopass init 0x0674104CA81A4905")
            } else {
                ProvResult(true, out = "gopass already configured")
            }
        }
        assertTrue(preparationResult.success)

        // when
        val res = a.task {
            installGopassBridgeJsonApi()
            configureGopassBridgeJsonApi()
        }

        // then
        assertTrue(res.success)
    }

    @ContainerTest
    @Test
    @NonCi
    @Disabled // long running test (> 1 min); if needed enable test and run manually
    fun test_install_GopassBridgeJsonApi_with_incompatible_gopass_jsonapi_version_installed() {
        // given
        val a = defaultTestContainer(ContainerStartMode.CREATE_NEW_KILL_EXISTING)
        val preparationResult = a.task {
            aptInstallCurl()

            configureGpgKeys(
                KeyPair(Secret(publicGPGSnakeoilKey()), Secret(privateGPGSnakeoilKey())),
                trust = true,
                skipIfExistin = false
            )
            installGopass("1.11.0", enforceVersion = true, "1ec9e0dfcfd9bcc241943e1a7d92f31bf3e66bb16f61ae5d079981325c31baa6")
            if (!chk("gopass ls")) {
                // configure gopass in default location with gpg-key-fingerprint of snakeoil keys
                cmd("printf \"\\ntest\\ntest@test.org\\n\" | gopass init 0x0674104CA81A4905")
            } else {
                ProvResult(true, out = "gopass already configured")
            }
        }
        assertTrue(preparationResult.success)

        // when
        val res = a.task {
            installGopassBridgeJsonApi()
            configureGopassBridgeJsonApi()
        }

        // then
        assertFalse(res.success)
    }

    @ContainerTest
    @Test
    @NonCi
    @Disabled // long running test (> 1 min); if needed enable test and run manually
    fun test_install_GopassBridgeJsonApi_with_incompatible_gopass_version_installed() {
        // given
        val a = defaultTestContainer(ContainerStartMode.CREATE_NEW_KILL_EXISTING)
        val preparationResult = a.task {
            aptInstallCurl()
            configureGpgKeys(
                KeyPair(Secret(publicGPGSnakeoilKey()), Secret(privateGPGSnakeoilKey())),
                trust = true,
                skipIfExistin = false
            )
            installGopass("1.9.0", enforceVersion = true, "fe13ef810d7fe200495107161e99eac081368aa0ce5e53971b1bd47a64eba4db")
            if (!chk("gopass ls")) {
                // configure gopass in default location with gpg-key-fingerprint of snakeoil keys
                cmd("printf \"\\ntest\\ntest@test.org\\n\" | gopass init 0x0674104CA81A4905")
            } else {
                ProvResult(true, out = "gopass already configured")
            }
        }
        assertTrue(preparationResult.success)

        // when
        val res = a.task {
            installGopassBridgeJsonApi()
            configureGopassBridgeJsonApi()
        }

        // then
        assertFalse(res.success)
    }

    private fun Prov.aptInstallCurl() = task {
        cmd("apt-get update", sudo = true)
        aptInstall("curl")
    }
}
