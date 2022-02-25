package org.domaindrivenarchitecture.provs.framework.extensions.server_software.standalone_server.firewall

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.docker.dockerProvideImage
import org.domaindrivenarchitecture.provs.framework.core.docker.dockerimages.UbuntuPlusUser
import org.domaindrivenarchitecture.provs.framework.core.docker.exitAndRmContainer
import org.domaindrivenarchitecture.provs.framework.core.local
import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerEndMode
import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerUbuntuHostProcessor
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.domaindrivenarchitecture.provs.test.tags.NonCi
import org.junit.jupiter.api.Assertions.assertTrue


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
        val a = Prov.newInstance(
            ContainerUbuntuHostProcessor(
                containerName,
                dockerImage.imageName(),
                ContainerStartMode.USE_RUNNING_ELSE_CREATE,  // already started in previous statement
                ContainerEndMode.EXIT_AND_REMOVE
            )
        )

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
