package org.domaindrivenarchitecture.provs.framework.ubuntu.user.domain

import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.createDir
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.createFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.fileContent
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.domain.KeyPairSource
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.domain.SshKeyPairSource
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.privateGPGSnakeoilKey
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.privateSSHRSASnakeoilKey
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.publicGPGSnakeoilKey
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.publicSSHRSASnakeoilKey
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.domain.SecretSourceType
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.infrastructure.configureUser
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.infrastructure.createUser
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.infrastructure.userExists
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.infrastructure.userIsInGroupSudo
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions

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
        Assertions.assertTrue(res.success)
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
        Assertions.assertTrue(res.success)
        Assertions.assertTrue(a.userExists(newUser))
        Assertions.assertTrue(!a.userIsInGroupSudo(newUser))
        Assertions.assertEquals("newdummykey", a.fileContent("/home/$newUser/.ssh/authorized_keys", sudo = true))
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
        Assertions.assertTrue(res.success)
        Assertions.assertTrue(prov.userExists(newUser))
        Assertions.assertEquals("newdummykey", prov.fileContent("/home/$newUser/.ssh/authorized_keys", sudo = true))

        // new user can sudo
        Assertions.assertTrue(prov.cmd("sudo -H -u $newUser bash -c 'sudo echo \"I am \$USER, with uid \$UID\"' ").success)
    }
}