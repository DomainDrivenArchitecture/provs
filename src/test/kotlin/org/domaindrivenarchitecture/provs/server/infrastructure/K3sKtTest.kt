package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.core.local
import org.domaindrivenarchitecture.provs.framework.core.remote
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDir
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createSecretFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.fileContent
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.secretSources.PromptSecretSource
import org.domaindrivenarchitecture.provs.server.domain.k3s.K3sConfig
import org.domaindrivenarchitecture.provs.server.domain.k3s.Node
import org.domaindrivenarchitecture.provs.server.domain.k3s.provisionK3s
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.lang.Thread.sleep

class K3sKtTest {

    @Test   // Extensive test, takes several minutes
    @Disabled("update remoteIp and user and run manually")
    fun installK3s() {
        // given
        val remoteHostIp = "192.168.56.146"
        val user = "xxx"

        // enable ssh connection either manually or by the commented-out code below to copy local authorized_keys to remote
//        remote(remoteHostIp, user, PromptSecretSource("PW for $user on $remoteHostIp").secret()).task {
//            val authorizedKeysFilename = ".ssh/authorized_keys"
//            val publicSshKey = local().getSecret("cat $authorizedKeysFilename") ?: Secret("") // or set directly by: val publicSshKey = Secret("public ssh key")
//            createDir(".ssh")
//            createSecretFile(authorizedKeysFilename, publicSshKey, posixFilePermission = "0644")
//        }

        // when
        val res = remote(remoteHostIp, user).task {        // connect by ssh
            provisionK3s(K3sConfig(remoteHostIp, Node(remoteHostIp), echo = true, reprovision = false))
        }

        // then
        assertTrue(res.success)

        // check response echo pod
        sleep(10000)  // if time too short, increase or check curl manually
        val echoResponse = local().cmd("curl http://$remoteHostIp/echo/").out
        assertTrue(echoResponse?.contains("Hostname: echo-app") ?: false)
        assertTrue(echoResponse?.contains("Host: $remoteHostIp") ?: false)
    }
}
