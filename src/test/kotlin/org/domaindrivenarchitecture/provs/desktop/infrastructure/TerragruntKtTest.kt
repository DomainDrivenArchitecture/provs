package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertTrue

class TerragruntKtTest {

  @ExtensiveContainerTest
 fun installTerragrunt() {
  // given
  val prov = defaultTestContainer()

  // when
  val res = prov.task {
   installOpentofu()
   installOpentofu()  // check repeatability
  }

  // then
  assertTrue(res.success)
 }
}
