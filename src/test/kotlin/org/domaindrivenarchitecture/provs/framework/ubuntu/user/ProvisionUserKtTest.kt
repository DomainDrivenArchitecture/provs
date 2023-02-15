package org.domaindrivenarchitecture.provs.framework.ubuntu.user

import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDir
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.fileContent
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.*
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.SecretSourceType
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.base.*
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue


internal class ProvisionUserKtTest {

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
                SshKeyPairSource(SecretSourceType.PLAIN, publicSSHRSASnakeoilKey(), privateSSHRSASnakeoilKey())
            )
        )

        // then
        assertTrue(res.success)
    }

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

    @ContainerTest
    fun createUserWithSudoAndCopiedSshKey() {
        // given
        val prov = defaultTestContainer()
        val newUser = "testnewsudouser4"
        prov.task {
            createDir(".ssh")
            createFile("~/.ssh/authorized_keys", "newdummykey")
        }

        // when
        val res = prov.createUser(newUser, userCanSudoWithoutPassword = true, copyAuthorizedSshKeysFromCurrentUser = true)

        // then
        assertTrue(res.success)
        assertTrue(prov.userExists(newUser))
        assertEquals("newdummykey", prov.fileContent("/home/$newUser/.ssh/authorized_keys", sudo = true))

        // new user can sudo
        assertTrue(prov.cmd("sudo -H -u $newUser bash -c 'sudo echo \"I am \$USER, with uid \$UID\"' ").success)
    }
}