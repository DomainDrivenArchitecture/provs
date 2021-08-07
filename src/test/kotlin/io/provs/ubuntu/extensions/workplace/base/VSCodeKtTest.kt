package io.provs.ubuntu.extensions.workplace.base

import io.provs.ubuntu.install.base.aptInstall
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import io.provs.test.defaultTestContainer


internal class VSCodeKtTest {

    @Test
    @Disabled("Test currently not working, needs fix. VSC is installed by snapd which is not currently supported to run inside docker")
    fun installVSC() {
        // given
        val a = defaultTestContainer()
        a.aptInstall("xvfb libgbm-dev libasound2")

        // when
        val res = a.installVSC("python", "clojure")

        // then
        assertTrue(res.success)
    }
}