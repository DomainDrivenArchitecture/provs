package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.core.remote
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.KeyPair
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base.configureGpgKeys
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base.gpgFingerprint
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.secretSources.GopassSecretSource
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.domaindrivenarchitecture.provs.test_keys.privateGPGSnakeoilKey
import org.domaindrivenarchitecture.provs.test_keys.publicGPGSnakeoilKey
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
        val a = defaultTestContainer()
        val gopassRootDir = ".password-store"
        a.aptInstall("wget git gnupg")
        a.createDir(gopassRootDir, "~/")
        a.cmd("git init", "~/$gopassRootDir")
        val fpr = a.gpgFingerprint(publicGPGSnakeoilKey())
        println("+++++++++++++++++++++++++++++++++++++ $fpr +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
        a.createFile("~/" + gopassRootDir + "/.gpg-id", fpr)

        a.createDir("exampleStoreFolder", "~/")
        a.createFile("~/exampleStoreFolder/.gpg-id", fpr)

        a.configureGpgKeys(KeyPair(Secret(publicGPGSnakeoilKey()), Secret(privateGPGSnakeoilKey())), true)

        // when
        val res = a.installGopass()
        val res2 = a.configureGopass(a.userHome() + gopassRootDir)
        val res3 = a.gopassMountStore("exampleStore", "~/exampleStoreFolder")

        // then
        a.fileContent("~/.config/gopass/config.yml")  // displays the content in the logs
        assertTrue(res.success)
        assertTrue(res2.success)
        assertTrue(res3.success)
        assertTrue(a.fileContainsText("~/.config/gopass/config.yml", "/home/testuser/.password-store"))
        assertTrue(a.fileContainsText("~/.config/gopass/config.yml", "exampleStore"))
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
        val a = remote(host, user)

        // when
        val res = a.task {
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
            installGopassBridgeJsonApi()
            configureGopassBridgeJsonApi()
        }

        // then
        assertTrue(res.success)
    }
}