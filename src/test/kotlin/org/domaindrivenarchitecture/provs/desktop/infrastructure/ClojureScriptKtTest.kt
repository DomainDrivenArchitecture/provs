package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class ClojureScriptKtTest {

    @Test
    @Disabled // does not run the first time, probably hanging due to "E: Could not get lock /var/lib/dpkg/lock-frontend. It is held by process 700 (apt-get)"
    fun installShadowCljs() {
        // given
        defaultTestContainer().cmd("sudo apt-get upgrade")

        // when
        val res = defaultTestContainer().installShadowCljs()

        // then
        assertTrue(res.success)
    }
}