package io.provs.platformTest

import io.provs.Prov
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS

internal class UbuntuProvTests {

    private val prov = Prov.defaultInstance()

    private fun ping(url: String) = prov.def {
        xec("ping", "-c", "4", url)
    }

    private fun outerPing() = prov.def {
        ping("gitlab.com")
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    fun that_ping_works() {
        // when
        val res = outerPing()

        // then
        assert(res.success)
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    fun that_cmd_works() {
        // given
        val a = Prov.defaultInstance()

        // when
        val res1 = a.cmd("pwd")
        val dir = res1.out?.trim()
        val res2 = a.cmd("echo abc", dir)

        // then
        assert(res1.success)
        assert(res2.success)
        assert(res2.out?.trim() == "abc")
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    fun that_nested_shells_work() {
        // given
        val a = Prov.defaultInstance()

        // when
        val res1 = a.cmd("pwd")
        val dir = res1.out?.trim()
        val res2 = a.cmd("echo abc", dir)

        // then
        assert(res1.success)
        assert(res2.success)
        assert(res2.out?.trim() == "abc")
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    fun that_xec_works() {
        // given
        val a = Prov.defaultInstance()

        // when
        val res1 = a.xec("/usr/bin/printf", "hi")
        val res2 = a.xec("/bin/ping", "-c", "2", "gitlab.com")
        val res3 = a.xec("/bin/bash", "-c", "echo echoed")

        // then
        assert(res1.success)
        assert(res1.out?.trim() == "hi")
        assert(res2.success)
        assert(res3.success)
        assert(res3.out?.trim() == "echoed")
    }

}

