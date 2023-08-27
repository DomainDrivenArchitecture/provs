package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.core.local
import org.domaindrivenarchitecture.provs.framework.core.remote
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDir
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createSecretFile
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
    @Disabled("1. update remoteIp and user, 2. enable remote ssh connection and 3. enable this test and then 4. run manually")
    fun installK3s() {
        // given
        val remoteIp = "192.168.56.146"
        val user = "xxx"

        // enable remote ssh connection either manually or by the commented-out code below to copy local authorized_keys to remote
//        remote(remoteIp, user, PromptSecretSource("PW for $user on $remoteIp").secret()).task {
//            val authorizedKeysFilename = ".ssh/authorized_keys"
//            val publicSshKey = local().getSecret("cat $authorizedKeysFilename") ?: Secret("") // or set directly by: val publicSshKey = Secret("public ssh key")
//            createDir(".ssh")
//            createSecretFile(authorizedKeysFilename, publicSshKey, posixFilePermission = "0644")
//        }

        // when
        val res = remote(remoteIp, user).task {        // connect by ssh
            provisionK3s(K3sConfig(remoteIp, Node(remoteIp), echo = true, reprovision = false))
        }

        // then
        assertTrue(res.success)

        // check response echo pod
        sleep(10000)  // if time too short, increase or check curl manually
        val echoResponse = local().cmd("curl http://$remoteIp/echo/").out
        assertTrue(echoResponse?.contains("Hostname: echo-app") ?: false)
        assertTrue(echoResponse?.contains("Host: $remoteIp") ?: false)
    }
}
