package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.remote
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.secretSources.PromptSecretSource
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class VSCodeKtTest {

    @Test
    @Disabled("Remote testing by updating connection details below, then enable test and run it manually.")
    // Remark: VSCode installs with snap, which does not run in container and cannot be tested by container test.
    fun installVSCode() {
        // given
        val prov = remote("192.168.56.153", "xx", PromptSecretSource("Remote password").secret())  // machine needs openssh-server installed and sudo possible without pw
        prov.aptInstall("xvfb libgbm-dev libasound2")

        // when
        val res = prov.task {
            installVSCode("python", "clojure")
            cmd("code -v")
            cmd("codium -v")
        }

        // then
        assertTrue(res.success)
    }
}