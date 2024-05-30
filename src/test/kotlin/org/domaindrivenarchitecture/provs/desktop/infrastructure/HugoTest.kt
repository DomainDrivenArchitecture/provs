package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.docker.exitAndRmContainer
import org.domaindrivenarchitecture.provs.framework.core.local
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class HugoTest {
    @ExtensiveContainerTest
    fun test_installHugoByDeb() {
        // given
        local().exitAndRmContainer("provs_test")
        val prov = defaultTestContainer()

        // when
        val res = prov.installHugoByDeb()

        // then
        assertTrue(res.success)
    }

    @Test
    fun test_needsHugoInstall() {
        // given
        val hugoNull = null
        val hugoLow = "hugo v0.0.0-abc+extended linux/amd64 BuildDate=0000-00-00 VendorInfo=snap:0.0.0"
        val hugoMajHigh = "hugo v1.0.0-abc+extended linux/amd64 BuildDate=0000-00-00 VendorInfo=snap:1.0.0"
        val hugoMinHigh = "hugo v0.1.0-abc+extended linux/amd64 BuildDate=0000-00-00 VendorInfo=snap:0.1.0"
        val hugoPatHigh = "hugo v0.0.1-abc+extended linux/amd64 BuildDate=0000-00-00 VendorInfo=snap:0.0.1"

        assertTrue(needsHugoInstall(hugoNull, hugoPatHigh))
        assertTrue(needsHugoInstall(hugoLow, hugoPatHigh))
        assertTrue(needsHugoInstall(hugoLow, hugoMinHigh))
        assertTrue(needsHugoInstall(hugoLow, hugoMajHigh))
        assertFalse(needsHugoInstall(hugoMajHigh, hugoLow))
        assertFalse(needsHugoInstall(hugoMinHigh, hugoLow))
        assertFalse(needsHugoInstall(hugoPatHigh, hugoLow))
    }
}