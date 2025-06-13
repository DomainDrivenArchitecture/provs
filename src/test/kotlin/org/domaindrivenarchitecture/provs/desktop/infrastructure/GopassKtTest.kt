package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.core.remote
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.domain.KeyPair
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.infrastructure.configureGpgKeys
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.infrastructure.gpgFingerprint
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.*
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.privateGPGSnakeoilKey
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.domain.secretSources.PromptSecretSource
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.infrastructure.makeCurrentUserSudoerWithoutPasswordRequired
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.domaindrivenarchitecture.provs.test_keys.publicGPGSnakeoilKey
import org.junit.jupiter.api.Assertions.assertFalse


internal class GopassKtTest {

    @ContainerTest
    fun test_configureGopass_fails_with_path_starting_with_tilde() {
        // when
        val res = defaultTestContainer().task {
            deleteFile(".config/gopass/config")
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
            gopassInitStoreFolder("~/exampleStoreFolder")  // check idem-potency
            gopassMountStore("exampleStore", "~/exampleStoreFolder")
            gopassMountStore("exampleStore", "~/exampleStoreFolder")  // check idem-potency
            prov.cmd("gopass ls")
            prov.cmd("gopass sync")
        }

        // then
        prov.fileContent("~/.config/gopass/config")  // displays the content in the logs
        assertTrue(res.success)
        assertTrue(prov.fileContainsText("~/.config/gopass/config", "/home/testuser/.password-store"))
        assertTrue(prov.fileContainsText("~/.config/gopass/config", "exampleStore"))
        assertTrue(prov.checkDir(".git", gopassRootDir))
    }

    @Test
    @Disabled  // This is an integration test, which needs preparation:
    // Pls change user, host and remote connection (choose connection either by password or by ssh key)
    // then remove tag @Disabled to be able to run this test.
    // PREREQUISITE: remote machine needs openssh-server installed
    fun test_install_and_configure_Gopass_and_GopassBridgeJsonApi() {
        // host and user
        val host = "192.168.56.154"
        val user = "xxx"

        // connection by password
        val pw = PromptSecretSource("Pw for $user").secret()
        val prov = remote(host, user, pw)
        prov.makeCurrentUserSudoerWithoutPasswordRequired(pw)  // may be commented out if user can already sudo without password

        // or alternatively use connection by ssh key if the public key is already available remotely
        // val prov = remote(host, user)


        val pubKey = Secret(publicGPGSnakeoilKey())
        val privateKey = Secret(privateGPGSnakeoilKey())


        // when
        val res = prov.task {
            configureGpgKeys(
                KeyPair(pubKey, privateKey),
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

