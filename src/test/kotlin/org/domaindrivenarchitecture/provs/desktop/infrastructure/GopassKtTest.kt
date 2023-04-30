package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.remote
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.KeyPair
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base.configureGpgKeys
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base.gpgFingerprint
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.secretSources.GopassSecretSource
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.*
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertFalse


internal class GopassKtTest {

    @ContainerTest
    fun test_configureGopass_fails_with_path_starting_with_tilde() {
        // when
        val res = defaultTestContainer().task {
            deleteFile(".config/gopass/config.yml")
            configureGopass("~/somedir")
        }

        // then
        assertFalse(res.success)
    }

    @ExtensiveContainerTest
    fun test_installAndConfigureGopassAndMountStore() {
        // given
        val prov = defaultTestContainer()
        val gopassRootDir = ".password-store"

        // when
        val res = prov.task("test_installAndConfigureGopassAndMountStore") {
            installGopass()
            configureGopass(prov.userHome() + gopassRootDir)
            gopassInitStoreFolder("~/exampleStoreFolder")
            gopassMountStore("exampleStore", "~/exampleStoreFolder")
            prov.cmd("gopass ls")
        }

        // then
        prov.fileContent("~/.config/gopass/config.yml")  // displays the content in the logs
        assertTrue(res.success)
        assertTrue(prov.fileContainsText("~/.config/gopass/config.yml", "/home/testuser/.password-store"))
        assertTrue(prov.fileContainsText("~/.config/gopass/config.yml", "exampleStore"))
    }

    @Test
    @Disabled  // Integrationtest; change user, host and keys, then remove this line to run this test
    fun test_install_and_configure_Gopass_and_GopassBridgeJsonApi() {
        // settings to change
        val host = "192.168.56.135"
        val user = "xxx"
        val pubKey = GopassSecretSource("path-to/pub.key").secret()
        val privateKey = GopassSecretSource("path-to/priv.key").secret()

        // given
        val prov = remote(host, user)

        // when
        val res = prov.task {
            configureGpgKeys(
                KeyPair(
                    pubKey,
                    privateKey
                ),
                trust = true,
                skipIfExistin = true
            )
            installGopass()

            if (!chk("gopass ls")) {
                // configure (=init) gopass
                cmd("printf \"\\ntest\\ntest@test.org\\n\" | gopass init " + gpgFingerprint(pubKey.plain())) // gopass init in default location with gpg-key-fingerprint of given key
            }
            downloadGopassBridge()
            installGopassJsonApi()
            configureGopassJsonApi()
        }

        // then
        assertTrue(res.success)
    }
}

