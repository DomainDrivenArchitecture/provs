package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDirs
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.fileContainsText
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class NetworkKtTest {

    @Test
    @ContainerTest
    fun test_provisionNetwork() {
        // given
        val p = defaultTestContainer(ContainerStartMode.CREATE_NEW_KILL_EXISTING)
        p.task {
            aptInstall("dbus netplan.io")
            createDirs("/etc/netplan", sudo = true)
            cmd("/etc/init.d/dbus start", sudo = true)
        }

        // when
        val res = p.provisionNetwork( "192.168.5.1", loopbackIpv6 = "fc00::5:1")

        // then
        // assertTrue(res.success) -- netplan is not working in an unprivileged container - see also https://askubuntu.com/questions/813588/systemctl-failed-to-connect-to-bus-docker-ubuntu16-04-container

        // check file content snippet
        assertTrue(p.fileContainsText("/etc/netplan/99-loopback.yaml", content = "- 192.168.5.1/32", sudo = true))
    }
}