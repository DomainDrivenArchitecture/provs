package org.domaindrivenarchitecture.provs.framework.ubuntu.firewall.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.docker.dockerProvideImage
import org.domaindrivenarchitecture.provs.framework.core.docker.dockerimages.UbuntuPlusUser
import org.domaindrivenarchitecture.provs.framework.core.docker.exitAndRmContainer
import org.domaindrivenarchitecture.provs.framework.core.local
import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerEndMode
import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerUbuntuHostProcessor
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.infrastructure.aptInstall
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.domaindrivenarchitecture.provs.test.tags.NonCi
import org.junit.jupiter.api.Assertions

internal class ProvisionFirewallKtTest {

    @ExtensiveContainerTest
    @NonCi
    fun configureFirewall() {
        // given
        val dockerImage = UbuntuPlusUser()
        local().dockerProvideImage(dockerImage)
        val containerName = "firewall_test"
        local().exitAndRmContainer(containerName)
        local().cmd("sudo docker run --cap-add=NET_ADMIN -dit --name $containerName ${dockerImage.imageName()}")
        val a = Prov.Factory.newInstance(
            ContainerUbuntuHostProcessor(
                containerName,
                dockerImage.imageName(),
                ContainerStartMode.USE_RUNNING_ELSE_CREATE,  // already started in previous statement
                ContainerEndMode.EXIT_AND_REMOVE
            )
        )

        // when
        val res = a.task {
            aptInstall("iptables")
            provisionFirewall()
        }
        local().exitAndRmContainer(containerName)

        // then
        Assertions.assertTrue(res.success)
    }
}