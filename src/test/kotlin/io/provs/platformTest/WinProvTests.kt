package io.provs.platformTest

import io.provs.Prov
import io.provs.test.testLocal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS

internal class WinProvTests {

    private fun Prov.ping(url: String) = def {
        cmd("ping $url")
    }

    private fun Prov.outerPing() = def { ping("nu.nl") }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    fun def_definesPing_function() {
        // when
        val res = testLocal().outerPing()

        // then
        assert(res.success)
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    fun cmd_executesCommand() {
        // given
        val a = testLocal()

        // when
        val res1 = a.cmd("echo %cd%")
        val dir = res1.out?.trim()
        val res2 = a.cmd("echo abc", dir)

        // then
        assert(res1.success)
        assert(res1.success)
        assertEquals( "abc", res2.out?.trim())
    }
}

