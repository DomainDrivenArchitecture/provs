package io.provs.ubuntu.extensions.workplace.base

import io.provs.core.Prov
import io.provs.core.ProvResult
import io.provs.core.Secret
import io.provs.core.docker.exitAndRmContainer
import io.provs.core.local
import io.provs.test.defaultTestContainer
import io.provs.test.tags.ContainerTest
import io.provs.test.tags.NonCi
import io.provs.ubuntu.install.base.aptInstall
import io.provs.ubuntu.keys.KeyPair
import io.provs.ubuntu.keys.base.configureGpgKeys
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import io.provs.ubuntu.extensions.test_keys.privateGPGSnakeoilKey
import io.provs.ubuntu.extensions.test_keys.publicGPGSnakeoilKey


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
        local().exitAndRmContainer("provs_test")
        val a = defaultTestContainer()
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
        local().exitAndRmContainer("provs_test")
        val a = defaultTestContainer()
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
