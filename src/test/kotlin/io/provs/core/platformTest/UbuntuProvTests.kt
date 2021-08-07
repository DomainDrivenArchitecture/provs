package io.provs.core.platformTest

import io.provs.core.Prov
import io.provs.test.tags.NonCi
import io.provs.test.testLocal
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS

internal class UbuntuProvTests {

    private fun Prov.ping(url: String) = def {
        xec("ping", "-c", "4", url)
    }

    private fun Prov.outerPing() = def {
        ping("gitlab.com")
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    fun that_ping_works() {
        // when
        val res = testLocal().outerPing()

        // then
        assert(res.success)
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    fun that_cmd_works() {
        // given
        val a = testLocal()

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
    @NonCi
    fun that_cmd_works_with_sudo() {
        // given
        val a = testLocal()

        // when
        val res1 = a.cmd("echo abc", "/root", sudo = true)

        // then
        assert(res1.success)
        assert(res1.out?.trim() == "abc")
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    fun that_nested_shells_work() {
        // given
        val a = testLocal()

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
        val a = testLocal()

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

