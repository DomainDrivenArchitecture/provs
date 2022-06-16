package org.domaindrivenarchitecture.provs.framework.ubuntu.web.base

import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.fileContent
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.test.tags.NonCi
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class WebKtTest {

    @ContainerTest
    fun downloadFromURL_downloadsFile() {
        // given
        val prov = defaultTestContainer()
        val file = "file1"
        prov.createFile("/tmp/" + file, "hello")

        // when
        val res = prov.downloadFromURL("file:///tmp/" + file, "file2", "/tmp")
        val res2 = prov.downloadFromURL("file:///tmp/" + file, "file2", "/tmp")
        val res3 = prov.downloadFromURL("file:///tmp/" + file, "file2", "/tmp", overwrite = true)

        // then
        val res4 = prov.fileContent("/tmp/file2")

        assertTrue(res.success)
        assertEquals("File /tmp/file2 already exists.", res2.out)
        assertTrue(res3.success)
        assertEquals(null, res3.out)
        assertEquals("hello", res4)
    }

    @ContainerTest
    fun downloadFromURL_local_file_with_correct_checksum() {
        // given
        val prov = defaultTestContainer()
        val srcFile = "file3.txt"
        val targetFile = "file3b.txt"
        prov.createFile("/tmp/" + srcFile, "hello")

        // when
        val res = prov.downloadFromURL("file:///tmp/" + srcFile, targetFile, "tmp", sha256sum ="2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824") // ubuntu 20.04 sha256sum version 8.30

        // then
        val res2 = prov.fileContent("tmp/$targetFile")

        assertEquals("hello", res2)
        assertTrue(res.success)
    }

    @ContainerTest
    fun downloadFromURL_local_file_with_incorrect_checksum() {
        // given
        val prov = defaultTestContainer()
        val srcFile = "file3.txt"
        val targetFile = "file3b.txt"
        prov.createFile("/tmp/" + srcFile, "hello")

        // when
        val res = prov.downloadFromURL("file:///tmp/" + srcFile, targetFile, "tmp", sha256sum = "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824WRONG", overwrite = true)

        // then
        val res2 = prov.checkFile("tmp/$targetFile")

        assertFalse(res.success)
        assertFalse(res2)
    }

    @ContainerTest
    @NonCi
    fun findIpForHostname_finds_ip() {
        // given
        val prov = defaultTestContainer()
        prov.aptInstall("dnsutils")

        // when
        val ip = prov.findIpForHostname("localhost")

        // then
        assertEquals("127.0.0.1", ip)
    }

    @ContainerTest
    @NonCi
    fun findIpForHostname_returns_null_if_ip_not_found() {
        // given
        val prov = defaultTestContainer()
        prov.aptInstall("dnsutils")

        // when
        val ip = prov.findIpForHostname("hostwhichisnotexisting")

        // then
        assertEquals(null, ip)
    }

    @ContainerTest
    @NonCi
    fun getIpForHostname_throws_exception_if_ip_not_found() {
        // given
        val prov = defaultTestContainer()
        prov.aptInstall("dnsutils")

        val exception = org.junit.jupiter.api.assertThrows<RuntimeException> {
            // when
            prov.getIpForHostname("hostwhichisnotexisting")
        }

        // then
        assertEquals("Could not resolve ip for: hostwhichisnotexisting", exception.message)
    }

    @Test
    fun isIp4_recognizes_ip_correctly() {
        // when
        val res1 = isIp4("123.123.123.123")
        val res2 = isIp4("123.abc.123.123")
        val res3 = isIp4("")

        // then
        assertTrue(res1)
        assertFalse(res2)
        assertFalse(res3)
    }

    @Test
    fun isIp6_recognizes_ip_correctly() {
        // when
        val res1 = isIp6("2001:db8:3333:4444:5555:6666:7777:8888")
        val res2 = isIp6("2001:db8:3333:4444:CCCC:DDDD:EEEE:FFFF")
        val res3 = isIp6("123.123.123.123")
        val res4 = isIp6("")

        // then
        assertTrue(res1)
        assertTrue(res2)
        assertFalse(res3)
        assertFalse(res4)
    }
}