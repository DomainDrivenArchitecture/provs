package io.provs.ubuntu.install.base

import io.provs.test.defaultTestContainer
import io.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test


internal class InstallKtTest {

    @ContainerTest
    @Test
    fun aptInstall_installsPackage() {
        // given
        val a = defaultTestContainer()

        // when
        val res = a.aptInstall("rolldice")

        // then
        assertTrue(res.success)
    }

    @ContainerTest
    @Test
    @Disabled  // run manually if needed;
    // todo: replace zim by a smaller repo
    fun aptInstallFromPpa_installsPackage() {
        // given
        val a = defaultTestContainer()
        a.aptInstall("software-properties-common")  // prereq for adding a repo to apt

        // when
        val res = a.aptInstallFromPpa("jaap.karssenberg", "zim", "zim")

        // then
        assertTrue(res.success)
    }
}