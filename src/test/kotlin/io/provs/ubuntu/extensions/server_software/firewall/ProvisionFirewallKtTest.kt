package io.provs.ubuntu.extensions.server_software.firewall

import io.provs.Prov

import io.provs.docker.dockerProvideImage
import io.provs.docker.exitAndRmContainer
import io.provs.docker.images.UbuntuPlusUser
import io.provs.local
import io.provs.processors.ContainerEndMode
import io.provs.processors.ContainerStartMode
import io.provs.processors.ContainerUbuntuHostProcessor
import io.provs.ubuntu.install.base.aptInstall
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test


internal class ProvisionFirewallKtTest {

    @Test
    @Disabled
    fun provisionFirewall() {
        // todo
    }

    @Test
    @Disabled
    fun resetFirewall() {
        // todo
    }

    @Test
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
