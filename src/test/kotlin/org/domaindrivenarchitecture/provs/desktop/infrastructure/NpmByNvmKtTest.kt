package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.assertTrue

internal class NpmByNvmKtTest {

 @ContainerTest
  fun installNVMnpm() {
     // given
     val container = defaultTestContainer(ContainerStartMode.CREATE_NEW_KILL_EXISTING)
     container.aptInstall("curl")
     // when
     val res01 = container.installNpmByNvm()
     val res02 = container.installNpmByNvm()
     // then
     assertTrue(res01.success)
     assertTrue(res02.success)
  }
}