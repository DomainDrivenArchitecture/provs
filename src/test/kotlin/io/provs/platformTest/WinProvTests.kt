package io.provs.platformTest

import io.provs.Prov
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS

internal class WinProvTests {

    private val prov = Prov.defaultInstance()

    private fun ping(url: String) = prov.def {
        cmd("ping $url")
    }

    private fun outerPing() = prov.def { ping("nu.nl") }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    fun def_definesPing_function() {
        // when
        val res = outerPing()

        // then
        assert(res.success)
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    fun cmd_executesCommand() {
        // given
        val a = Prov.defaultInstance()

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

