package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.getResourceAsText
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.infrastructure.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.*
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled

internal class DevOpsKtTest {

    @ExtensiveContainerTest
    fun installKubectlAndTools() {
        // given
        defaultTestContainer().task {
            createDirs("/etc/bash_completion.d", sudo = true)
            createDir(".bashrc.d")
        }

        //when
        val res = defaultTestContainer().installKubectlAndTools()

        // then
        assertTrue(res.success)
        assertTrue(
            defaultTestContainer().fileContainsText(
                "~/.bashrc.d/kubectl.sh",
                getResourceAsText("org/domaindrivenarchitecture/provs/desktop/infrastructure/kubectl.sh")
            )
        )
        assertTrue(
            defaultTestContainer().checkFile("/etc/bash_completion.d/kubernetes", sudo = true)
        )
    }

    @ExtensiveContainerTest
    @Disabled("Part of test installKubectlAndTools, but can be tested separately by this test if required")
    fun installKubectl() {
        // given
        val prov = defaultTestContainer()

        // when
        val res = prov.installKubectl()

        // then
        assertTrue(res.success)
    }

    @ExtensiveContainerTest
    fun installKubeconform() {
        // given
        val prov = defaultTestContainer()

        // when
        val res = prov.installKubeconform()

        // then
        assertTrue(res.success)
        assertTrue(prov.checkFile("/usr/local/bin/kubeconform"))
    }

    @ExtensiveContainerTest
    fun installTerraform() {
        // given
        val prov = defaultTestContainer()

        // when
        val res = prov.task {
            aptInstall("git curl unzip")
            installTerraform()
            installTerraform()  // check repeatability
        }

        // then
        assertTrue(res.success)
    }

    @ExtensiveContainerTest
    fun installDirenv() {
        // given
        val prov = defaultTestContainer()

        // when
        val res = prov.task {
            installDirenv()
            installDirenv()  // check repeatability
        }

        // then
        assertTrue(res.success)
    }
}
