package org.domaindrivenarchitecture.provs.ubuntu.extensions.server_software.firewall

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.docker.dockerProvideImage
import org.domaindrivenarchitecture.provs.core.docker.dockerimages.UbuntuPlusUser
import org.domaindrivenarchitecture.provs.core.docker.exitAndRmContainer
import org.domaindrivenarchitecture.provs.core.local
import org.domaindrivenarchitecture.provs.core.processors.ContainerEndMode
import org.domaindrivenarchitecture.provs.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.core.processors.ContainerUbuntuHostProcessor
import org.domaindrivenarchitecture.provs.test.tags.NonCi
import org.domaindrivenarchitecture.provs.ubuntu.install.base.aptInstall
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
