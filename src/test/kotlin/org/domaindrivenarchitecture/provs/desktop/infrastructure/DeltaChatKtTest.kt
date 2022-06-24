package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.assertTrue

class DeltaChatKtTest {

    @ExtensiveContainerTest
    fun installDeltaChat() {
        // given
        val a = defaultTestContainer()
        // when
        val res = a.task { installDeltaChat() }
        // then
        assertTrue(res.success)
    }

}
