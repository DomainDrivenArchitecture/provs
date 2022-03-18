package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.getResourceAsText
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDir
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDirs
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.fileContainsText
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertTrue

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
            defaultTestContainer().fileContainsText(
                "/etc/bash_completion.d/kubernetes",
                "\nkubectl completion bash\n"
            )
        )
    }
}
