package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.assertTrue

internal class NpmByNvmKtTest {

    @ContainerTest
    fun installNpmByNvm() {
        // given
        val prov = defaultTestContainer(ContainerStartMode.CREATE_NEW_KILL_EXISTING)
        prov.aptInstall("curl")

        // when
        val res = prov.task {
            installNpmByNvm()
            installNpmByNvm()  // check repeatability
            // check if node and npm are installed and can be called in a shell (with sourcing nvm.sh)
            cmd(". .nvm/nvm.sh && node -v")
            cmd(". .nvm/nvm.sh && npm --version")
        }

        // then
        assertTrue(res.success)
    }
}