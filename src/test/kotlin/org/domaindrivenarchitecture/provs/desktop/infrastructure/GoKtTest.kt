package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.assertTrue

class GoKtTest {

@ContainerTest
 fun installGo() {
 // given
 val container = defaultTestContainer()
  // when
 val res01 = container.installGo()
 val res02 = container.installGo()
 // then
 assertTrue(res01.success)
 assertTrue(res02.success)
 }
}