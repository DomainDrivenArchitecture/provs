package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.deleteFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.fileContainsText
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertTrue

class SshKtTest {

    @ExtensiveContainerTest
    fun test_configureSsh() {

        // given
        val prov = defaultTestContainer()
        prov.task {
            aptInstall("openssh-server")
            deleteFile(pathSshdHardeningConfig, sudo = true)
        }

        // when
        prov.configureSsh()

        // then

        // Note: result of method configureSsh might have status failure as restart ssh within docker is not possible,
        // but files should have expected content
        assertTrue(prov.fileContainsText("/etc/ssh/ssh_config","PasswordAuthentication no", sudo=true))
        assertTrue(prov.fileContainsText("/etc/ssh/sshd_config","PasswordAuthentication no", sudo=true))
        assertTrue(prov.checkFile("/etc/ssh/sshd_config.d/sshd_hardening.conf"))
    }
}