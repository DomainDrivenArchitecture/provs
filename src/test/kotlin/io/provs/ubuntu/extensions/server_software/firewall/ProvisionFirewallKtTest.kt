package io.provs.ubuntu.extensions.server_software.firewall

import io.provs.core.Prov
import io.provs.core.docker.dockerProvideImage
import io.provs.core.docker.dockerimages.UbuntuPlusUser
import io.provs.core.docker.exitAndRmContainer
import io.provs.core.local
import io.provs.core.processors.ContainerEndMode
import io.provs.core.processors.ContainerStartMode
import io.provs.core.processors.ContainerUbuntuHostProcessor
import io.provs.test.tags.NonCi
import io.provs.ubuntu.install.base.aptInstall
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


internal class ProvisionFirewallKtTest {

    @Test
    @NonCi
    fun configureFirewall() {
        // given
        val dockerImage = UbuntuPlusUser()
        local().dockerProvideImage(dockerImage)
        val containerName = "firewall_test"
        local().exitAndRmContainer(containerName)
        local().cmd("sudo docker run --cap-add=NET_ADMIN -dit --name $containerName ${dockerImage.imageName()}")
        val a = Prov.newInstance(
            ContainerUbuntuHostProcessor(
                containerName,
                dockerImage.imageName(),
                ContainerStartMode.USE_RUNNING_ELSE_CREATE,  // already started in previous statement
                ContainerEndMode.EXIT_AND_REMOVE
            ))

        // when
        val res = a.requireAll {
            aptInstall("iptables")
            provisionFirewall()
        }
        local().exitAndRmContainer(containerName)

        // then
        assertTrue(res.success)
    }
}
