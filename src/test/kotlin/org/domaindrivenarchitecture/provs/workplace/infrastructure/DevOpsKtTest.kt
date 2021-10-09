package org.domaindrivenarchitecture.provs.workplace.infrastructure

import org.domaindrivenarchitecture.provs.core.getResourceAsText
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.createDir
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.createDirs
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.fileContainsText
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class DevOpsKtTest {

    @Test
    fun installKubectlAndTools() {
        // given
        defaultTestContainer().def {
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
                getResourceAsText("workplace/infrastructure/kubectl.sh")
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
