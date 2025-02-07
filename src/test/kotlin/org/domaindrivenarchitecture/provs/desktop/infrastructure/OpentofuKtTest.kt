package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertTrue

class OpentofuKtTest {

 @ExtensiveContainerTest
 fun installOpentofu() {
  // given
  val prov = defaultTestContainer()

  // when
  val res = prov.task {

   aptInstall("gnupg curl")
   installOpentofu()
   installOpentofu()  // check repeatability
  }

  // then
  assertTrue(res.success)
 }
}