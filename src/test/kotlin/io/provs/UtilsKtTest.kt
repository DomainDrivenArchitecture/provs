package io.provs

import io.provs.test.defaultTestContainer
import io.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.net.UnknownHostException

internal class UtilsKtTest {

    @Test
    fun test_getCallingMethodName() {
        // when
        val s = getCallingMethodName()

        // then
        assertEquals("test_getCallingMethodName", s)
    }

    @Test
    @ContainerTest
    fun runCmdInContainer() {
        // when
        val res = defaultTestContainer().cmd("echo something")

        // then
        assertTrue(res.success)
    }

    @Test
    fun remote_emptyHost() {
        assertThrows(IllegalArgumentException::class.java,
            { remote("", "user") })
    }

    @Test
    fun remote_invalidHost() {
        assertThrows(
            UnknownHostException::class.java,
            { remote("invalid_host", "user") })
    }

    @Test
    @Disabled // run manually after having updated user
    fun test_remote() {
        assertTrue(remote("127.0.0.1", "user").cmd("echo sth").success)
    }
}