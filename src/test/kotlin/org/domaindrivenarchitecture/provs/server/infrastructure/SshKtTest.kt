package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.fileContainsText
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertTrue

class SshKtTest {

    @ExtensiveContainerTest
    fun test_configureSsh() {

        // given
        val p = defaultTestContainer()

        // when
        val res = p.configureSsh()

        // then
        assertTrue(res.success)
        assertTrue(p.fileContainsText("/etc/ssh/ssh_config","PasswordAuthentication no", sudo=true))
        assertTrue(p.fileContainsText("/etc/ssh/sshd_config","PasswordAuthentication no", sudo=true))
        assertTrue(p.checkFile("/etc/ssh/sshd_config.d/sshd_hardening.conf"))
    }
}