package org.domaindrivenarchitecture.provs.core

import org.domaindrivenarchitecture.provs.framework.core.getCallingMethodName
import org.domaindrivenarchitecture.provs.framework.core.getLocalFileContent
import org.domaindrivenarchitecture.provs.framework.core.getResourceAsText
import org.domaindrivenarchitecture.provs.framework.core.remote
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
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
    fun getResourceAsText_successful() {
        assertEquals("resource text\n", getResourceAsText("resource-test"))
    }

    @Test
    fun getResourceAsText_throws_exception_for_missing_file() {
        assertThrows<IllegalArgumentException> {
            getResourceAsText("not existing resource")
        }
    }

    @Test
    fun getLocalFileContent_successful() {
        val resourcesDirectory = File("src/test/resources").absolutePath
        assertEquals("resource text\n", getLocalFileContent("$resourcesDirectory/resource-test"))
    }

    @Test
    @Disabled // run manually after having updated user
    fun test_remote() {
        assertTrue(remote("127.0.0.1", "user").cmd("echo sth").success)
    }
}