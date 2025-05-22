package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.core.docker.exitAndRmContainer
import org.domaindrivenarchitecture.provs.framework.core.local
import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.KeyPair
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base.configureGpgKeys
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
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
        val prov = defaultTestContainer()

        // when
        val res = prov.downloadGopassBridge()

        // then
        assertTrue(res.success)
    }

    @ExtensiveContainerTest
    @NonCi
    // test may fail sometimes with: §System has not been booted with systemd as init system (PID 1). Can't operate.\nFailed to connect to bus: Host is down"
    fun test_install_and_configure_GopassBridgeJsonApi() {
        // given
        local().exitAndRmContainer("provs_test")
        val prov = defaultTestContainer()
        val preparationResult = prov.task {
            configureGpgKeys(
                KeyPair(Secret(publicGPGSnakeoilKey()), Secret(privateGPGSnakeoilKey())),
                trust = true,
                skipIfExistin = false
            )
            installGopass()
            configureGopass(publicGpgKey = Secret(publicGPGSnakeoilKey()))
        }
        assertTrue(preparationResult.success)

        // when
        val res = prov.task {
            installGopassJsonApi()
            configureGopassJsonApi()
        }

        // then
        assertTrue(res.success)
    }

    @ExtensiveContainerTest
    @Test
    @NonCi
    @Disabled // long running test (> 1 min); if needed enable test and run manually
    fun test_install_GopassBridgeJsonApi_with_incompatible_gopass_jsonapi_version_installed() {
        // given
        val prov = defaultTestContainer(ContainerStartMode.CREATE_NEW_KILL_EXISTING)
        val preparationResult = prov.task {
            configureGpgKeys(
                KeyPair(Secret(publicGPGSnakeoilKey()), Secret(privateGPGSnakeoilKey())),
                trust = true,
                skipIfExistin = false
            )
            installGopass("1.11.0", enforceUpgrade = true, "1ec9e0dfcfd9bcc241943e1a7d92f31bf3e66bb16f61ae5d079981325c31baa6")
            configureGopass(publicGpgKey = Secret(publicGPGSnakeoilKey()))
        }
        assertTrue(preparationResult.success)

        // when
        val res = prov.task {
            installGopassJsonApi()
            configureGopassJsonApi()
        }

        // then
        assertFalse(res.success)
    }

    @ExtensiveContainerTest
    @Test
    @NonCi
    @Disabled // long running test (> 1 min); if needed, enable test and run manually
    fun test_install_GopassBridgeJsonApi_with_incompatible_gopass_version_installed() {
        // given
        val prov = defaultTestContainer(ContainerStartMode.CREATE_NEW_KILL_EXISTING)
        val preparationResult = prov.task {
            configureGpgKeys(
                KeyPair(Secret(publicGPGSnakeoilKey()), Secret(privateGPGSnakeoilKey())),
                trust = true,
                skipIfExistin = false
            )
            installGopass("1.9.0", enforceUpgrade = true, "fe13ef810d7fe200495107161e99eac081368aa0ce5e53971b1bd47a64eba4db")
            configureGopass(publicGpgKey = Secret(publicGPGSnakeoilKey()))
        }
        assertTrue(preparationResult.success)

        // when
        val res = prov.task {
            installGopassJsonApi()
            configureGopassJsonApi()
        }

        // then
        assertFalse(res.success)
    }
}
