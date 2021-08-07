package org.domaindrivenarchitecture.provs.extensions.workplace.base

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.ProvResult
import org.domaindrivenarchitecture.provs.core.Secret
import org.domaindrivenarchitecture.provs.core.docker.exitAndRmContainer
import org.domaindrivenarchitecture.provs.core.local
import org.domaindrivenarchitecture.provs.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.test.tags.NonCi
import org.domaindrivenarchitecture.provs.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.ubuntu.keys.KeyPair
import org.domaindrivenarchitecture.provs.ubuntu.keys.base.configureGpgKeys
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.domaindrivenarchitecture.provs.extensions.test_keys.privateGPGSnakeoilKey
import org.domaindrivenarchitecture.provs.extensions.test_keys.publicGPGSnakeoilKey


internal class GopassBridgeKtTest {

    @ContainerTest
    @Test
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

    @ContainerTest
    @Test
    fun test_install_and_configure_GopassBridgeJsonApi() {
        // given
        local().exitAndRmContainer("provs_test")
        val a = defaultTestContainer()
        val preparationResult = a.def {
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
        val res = a.def {
            installGopassBridgeJsonApi()
            configureGopassBridgeJsonApi()
        }

        // then
        assertTrue(res.success)
    }

    @ContainerTest
    @Test
    @NonCi
    fun test_install_GopassBridgeJsonApi_with_incompatible_gopass_jsonapi_version_installed() {
        // given
        val a = defaultTestContainer(ContainerStartMode.CREATE_NEW_KILL_EXISTING)
        val preparationResult = a.def {
            aptInstallCurl()

            configureGpgKeys(
                KeyPair(Secret(publicGPGSnakeoilKey()), Secret(privateGPGSnakeoilKey())),
                trust = true,
                skipIfExistin = false
            )
            installGopass("1.11.0", enforceVersion = true)
            if (!chk("gopass ls")) {
                // configure gopass in default location with gpg-key-fingerprint of snakeoil keys
                cmd("printf \"\\ntest\\ntest@test.org\\n\" | gopass init 0x0674104CA81A4905")
            } else {
                ProvResult(true, out = "gopass already configured")
            }
        }
        assertTrue(preparationResult.success)

        // when
        val res = a.def {
            installGopassBridgeJsonApi()
            configureGopassBridgeJsonApi()
        }

        // then
        assertFalse(res.success)
    }

    @ContainerTest
    @Test
    @NonCi
    fun test_install_GopassBridgeJsonApi_with_incompatible_gopass_version_installed() {
        // given
        val a = defaultTestContainer(ContainerStartMode.CREATE_NEW_KILL_EXISTING)
        val preparationResult = a.def {
            aptInstallCurl()
            configureGpgKeys(
                KeyPair(Secret(publicGPGSnakeoilKey()), Secret(privateGPGSnakeoilKey())),
                trust = true,
                skipIfExistin = false
            )
            installGopass("1.9.0", enforceVersion = true)
            if (!chk("gopass ls")) {
                // configure gopass in default location with gpg-key-fingerprint of snakeoil keys
                cmd("printf \"\\ntest\\ntest@test.org\\n\" | gopass init 0x0674104CA81A4905")
            } else {
                ProvResult(true, out = "gopass already configured")
            }
        }
        assertTrue(preparationResult.success)

        // when
        val res = a.def {
            installGopassBridgeJsonApi()
            configureGopassBridgeJsonApi()
        }

        // then
        assertFalse(res.success)
    }

    private fun Prov.aptInstallCurl() = def {
        cmd("apt-get update", sudo = true)
        aptInstall("curl")
    }
}
