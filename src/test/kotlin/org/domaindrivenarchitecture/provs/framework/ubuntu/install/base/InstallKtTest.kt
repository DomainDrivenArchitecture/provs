package org.domaindrivenarchitecture.provs.framework.ubuntu.install.base

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled


internal class InstallKtTest {

    @ContainerTest
    fun aptInstall_installsPackage() {
        // given
        val a = defaultTestContainer()

        // when
        val res = a.aptInstall("rolldice")

        // then
        assertTrue(res.success)
    }

    @ContainerTest
    fun aptInstall_ignores_packages_already_installed() {
        // given
        val a = defaultTestContainer()

        // when
        val res = a.aptInstall("sed grep")

        // then
        assertTrue(res.success)
        assertEquals("All packages are already installed. [sed grep]", res.out)
    }

    @ContainerTest
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