package org.domaindrivenarchitecture.provs.framework.ubuntu.user

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDir
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.fileContent
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.*
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.SecretSourceType
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.base.configureUser
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.base.createUser
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.base.userExists
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.base.userIsInGroupSudo
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


internal class ProvisionUserKtTest {

    @Test
    @ExtensiveContainerTest
    fun configureUser() {
        // given
        val a = defaultTestContainer()

        // when
        val res = a.configureUser(
            UserConfig(
                "testuser",
                "test@mail.com",
                KeyPairSource(SecretSourceType.PLAIN, publicGPGSnakeoilKey(), privateGPGSnakeoilKey()),
                KeyPairSource(SecretSourceType.PLAIN, publicSSHSnakeoilKey(), privateSSHSnakeoilKey())
            )
        )

        // then
        assert(res.success)
    }

    @Test
    @ContainerTest
    fun createUser() {
        // given
        val a = defaultTestContainer()
        val newUser = "testnewuser3"
        a.task {
            createDir(".ssh")
            createFile("~/.ssh/authorized_keys", "newdummykey")
        }

        // when
        val res = a.createUser(newUser, copyAuthorizedSshKeysFromCurrentUser = true)

        // then
        assertTrue(res.success)
        assertTrue(a.userExists(newUser))
        assertTrue(!a.userIsInGroupSudo(newUser))
        assertEquals("newdummykey", a.fileContent("/home/$newUser/.ssh/authorized_keys", sudo = true))
    }

    @Test
    @ContainerTest
    fun createUserWithSudo() {
        // given
        val a = defaultTestContainer()
        val newUser = "testnewsudouser3"
        a.task {
            createDir(".ssh")
            createFile("~/.ssh/authorized_keys", "newdummykey")
        }

        // when
        val res = a.createUser(newUser, sudo = true, copyAuthorizedSshKeysFromCurrentUser = true)

        // then
        assertTrue(res.success)
        assertTrue(a.userExists(newUser))
        assertEquals("newdummykey", a.fileContent("/home/$newUser/.ssh/authorized_keys", sudo = true))

        // new user can sudo
        assertTrue(a.cmd("sudo -H -u $newUser bash -c 'sudo echo \"I am \$USER, with uid \$UID\"' ").success)
    }
}