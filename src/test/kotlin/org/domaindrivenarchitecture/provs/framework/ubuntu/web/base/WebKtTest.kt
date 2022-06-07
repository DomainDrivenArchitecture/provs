package org.domaindrivenarchitecture.provs.framework.ubuntu.web.base

import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.fileContent
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class WebKtTest {

    @ContainerTest
    @Test
    fun downloadFromURL_downloadsFile() {
        // given
        val a = defaultTestContainer()
        val file = "file1"
        a.createFile("/tmp/" + file, "hello")

        // when
        val res = a.downloadFromURL("file:///tmp/" + file, "file2", "/tmp")
        val res2 = a.downloadFromURL("file:///tmp/" + file, "file2", "/tmp")
        val res3 = a.downloadFromURL("file:///tmp/" + file, "file2", "/tmp", overwrite = true)

        // then
        val res4 = a.fileContent("/tmp/file2")

        assertTrue(res.success)
        assertEquals("File /tmp/file2 already exists.", res2.out)
        assertTrue(res3.success)
        assertEquals(null, res3.out)
        assertEquals("hello", res4)
    }

    @ContainerTest
    @Test
    fun downloadFromURL_local_file_with_correct_checksum() {
        // given
        val a = defaultTestContainer()
        val srcFile = "file3.txt"
        val targetFile = "file3b.txt"
        a.createFile("/tmp/" + srcFile, "hello")

        // when
        val res = a.downloadFromURL("file:///tmp/" + srcFile, targetFile, "tmp", sha256sum ="2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824") // ubuntu 20.04 sha256sum version 8.30

        // then
        val res2 = a.fileContent("tmp/$targetFile")

        assertEquals("hello", res2)
        assertTrue(res.success)
    }

    @ContainerTest
    @Test
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
    fun findIpForHostname_returns_null_if_ip_not_found() {
        // given
        val prov = defaultTestContainer()
        prov.aptInstall("dnsutils")

        // when
        val ip = prov.findIpForHostname("hostwhichisnotexisting")

        // then
        assertEquals(null, ip)
    }

    @Test
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
}