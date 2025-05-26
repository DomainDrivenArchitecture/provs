package org.domaindrivenarchitecture.provs.server.infrastructure

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.domaindrivenarchitecture.provs.framework.core.*
import org.domaindrivenarchitecture.provs.framework.core.processors.DummyProcessor
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFile
import org.domaindrivenarchitecture.provs.server.domain.k3s.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File
import java.lang.Thread.sleep


class K3sKtTest {

    @AfterEach
    internal fun afterEach() {
        // cleanup
        unmockkAll()
    }

    @Test
    fun test_provisionK3sApplication() {
        // given
        val dummyProv = Prov.newInstance(DummyProcessor())
        mockkStatic(Prov::createFile)
        mockkStatic(Prov::applyK3sFile)
        every { any<Prov>().createFile(any(), any()) } returns ProvResult(true, cmd = "mocked")
        every { any<Prov>().applyK3sFile(any()) } returns ProvResult(true, cmd = "mocked")

        // when
        dummyProv.provisionK3sApplication(ApplicationFile(ApplicationFileName("../folder/test.yaml"), "testcontent"))

        // then
        verify(exactly = 1) { any<Prov>().createFile("/etc/rancher/k3s/manifests/test.yaml", "testcontent", "644", true ) }
        verify(exactly = 1) { any<Prov>().applyK3sFile(File("/etc/rancher/k3s/manifests/test.yaml")) }
    }

    @Test   // Extensive test, takes several minutes
    @Disabled("1. update remoteIp and user, 2. enable remote ssh connection and 3. enable this test and then 4. run manually")
    fun test_installK3s() {
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

    @Test   // Extensive test, takes several minutes
    @Disabled("1. update remoteIp and user, 2. enable remote ssh connection and 3. enable this test and then 4. run manually")
    fun test_installK3sWithApplicationFiles() {
        // given
        val remoteIp = "192.168.56.153"
        val user = "xxx"

        // enable remote ssh connection either manually or by the commented-out code below to copy local authorized_keys to remote
        // setting up connection by ssh is recommended as k3s installation might disable ssh by password
//        remote(remoteIp, user, PromptSecretSource("PW for $user on $remoteIp").secret()).task {
//            val authorizedKeysFilename = ".ssh/authorized_keys"
//            val publicSshKey = local().getSecret("cat $authorizedKeysFilename") ?: Secret("") // or set directly by: val publicSshKey = Secret("public ssh key")
//            createDir(".ssh")
//            createSecretFile(authorizedKeysFilename, publicSshKey, posixFilePermission = "0644")
//        }

        // alternative connection by ssh (authorized key must be installed remotely)
        val remoteServer = remote(remoteIp, user)

        // when
        val res = remoteServer.task {
            provisionK3s(
                K3sConfig(remoteIp, Node(remoteIp), echo = true, reprovision = false), applicationFiles = listOf(
                    ApplicationFile(ApplicationFileName("test-application-a.yaml"), getResourceAsText("test-application-a.yaml")),
                    ApplicationFile(ApplicationFileName("test-application-b.yaml"), getResourceAsText("test-application-b.yaml")),
                )
            )
        }

        // then
        assertTrue(res.success)

        // check response echo pod
        sleep(10000)  // if time too short, increase or check curl manually
        val echoResponse = local().cmd("curl http://$remoteIp/echo/").out
        assertTrue(echoResponse?.contains("Hostname: echo-app") ?: false)
        assertTrue(echoResponse?.contains("Host: $remoteIp") ?: false)

        // check application file deployment
        val namepsaces = remoteServer.cmd("sudo k3s kubectl get ns").out
        assertTrue(namepsaces?.contains("test-namespace-a") ?: false, "test-namespace-a not found in $namepsaces")
        assertTrue(namepsaces?.contains("test-namespace-b") ?: false, "test-namespace-b not found in $namepsaces")
    }
}
