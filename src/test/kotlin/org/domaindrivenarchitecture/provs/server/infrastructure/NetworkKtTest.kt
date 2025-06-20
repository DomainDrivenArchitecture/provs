package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.createDirs
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.fileContainsText
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.infrastructure.aptInstall
import org.domaindrivenarchitecture.provs.server.domain.CertmanagerEndpoint
import org.domaindrivenarchitecture.provs.server.domain.k3s.Certmanager
import org.domaindrivenarchitecture.provs.server.domain.k3s.K3sConfig
import org.domaindrivenarchitecture.provs.server.domain.k3s.Loopback
import org.domaindrivenarchitecture.provs.server.domain.k3s.Node
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertTrue

internal class NetworkKtTest {

    @Suppress("unused")
    @ExtensiveContainerTest
    fun test_provisionNetwork() {
        // given
        val p = defaultTestContainer()
        p.task {
            aptInstall("dbus netplan.io")
            createDirs("/etc/netplan", sudo = true)
            cmd("/etc/init.d/dbus start", sudo = true)
        }

        // when
        @Suppress("UNUSED_VARIABLE")  // see comments below: about netplan not working in unprivileged container++++
        val res = p.provisionNetwork(
            K3sConfig(
                fqdn = "statistics.test.meissa-gmbh.de",
                node = Node("162.55.164.138", "2a01:4f8:c010:672f::1"),
                loopback = Loopback("192.168.5.1", "fc00::5:1"),
                certmanager = Certmanager(
                    email = "admin@meissa-gmbh.de",
                    letsencryptEndpoint = CertmanagerEndpoint.prod
                ),
                echo = true,
                reprovision = true
            )
        )

        // then
        // assertTrue(res.success) -- netplan is not working in an unprivileged container - see also https://askubuntu.com/questions/813588/systemctl-failed-to-connect-to-bus-docker-ubuntu16-04-container

        // check file content snippet
        assertTrue(p.fileContainsText("/etc/netplan/99-loopback.yaml", content = "- 192.168.5.1/32", sudo = true))
    }
}