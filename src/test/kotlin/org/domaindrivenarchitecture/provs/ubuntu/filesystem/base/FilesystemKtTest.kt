package org.domaindrivenarchitecture.provs.ubuntu.filesystem.base

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


internal class FilesystemKtTest {

    @Test
    @ContainerTest
    fun checkingCreatingDeletingFile() {
        // given
        val prov = defaultTestContainer()

        // when
        val res1 = prov.fileExists("testfile")
        val res2 = prov.createFile("testfile", "some content")
        val res3 = prov.fileExists("testfile")
        val res4a = prov.fileContainsText("testfile", "some content")
        val res4b = prov.fileContainsText("testfile", "some non-existing content")
        val res5 = prov.deleteFile("testfile")
        val res6 = prov.fileExists("testfile")

        // then
        assertFalse(res1)
        assertTrue(res2.success)
        assertTrue(res3)
        assertTrue(res4a)
        assertFalse(res4b)
        assertTrue(res5.success)
        assertFalse(res6)
    }


    @Test
    @ContainerTest
    fun checkingCreatingDeletingFileWithSudo() {
        // given
        val prov = defaultTestContainer()

        // when
        val file = "/testfile"
        val res1 = prov.fileExists(file)
        val res2 = prov.createFile(file, "some content", sudo = true)
        val res3 = prov.fileExists(file)
        val res4a = prov.fileContainsText(file, "some content")
        val res4b = prov.fileContainsText(file, "some non-existing content")
        val res5 = prov.deleteFile(file)
        val res6 = prov.fileExists(file)
        val res7 = prov.deleteFile(file, true)
        val res8 = prov.fileExists(file)

        // then
        assertFalse(res1)
        assertTrue(res2.success)
        assertTrue(res3)
        assertTrue(res4a)
        assertFalse(res4b)
        assertFalse(res5.success)
        assertTrue(res6)
        assertTrue(res7.success)
        assertFalse(res8)
    }


    @Test
    @ContainerTest
    fun checkingCreatingDeletingDir() {
        // given
        val prov = defaultTestContainer()

        // when
        val res1 = prov.dirExists("testdir")
        val res2 = prov.createDir("testdir", "~/")
        val res3 = prov.dirExists("testdir")
        val res4 = prov.deleteDir("testdir", "~/")
        val res5 = prov.dirExists("testdir")

        val res6 = prov.dirExists("testdir", "~/test")
        val res7 = prov.createDirs("test/testdir")
        val res8 = prov.dirExists("testdir", "~/test")
        prov.deleteDir("testdir", "~/test/")

        // then
        assertFalse(res1)
        assertTrue(res2.success)
        assertTrue(res3)
        assertTrue(res4.success)
        assertFalse(res5)
        assertFalse(res6)
        assertTrue(res7.success)
        assertTrue(res8)
    }


    @Test
    @ContainerTest
    fun checkingCreatingDeletingDirWithSudo() {
        // given
        val prov = defaultTestContainer()

        // when
        val res1 = prov.dirExists("/testdir", sudo = true)
        val res2 = prov.createDir("testdir", "/", sudo = true)
        val res3 = prov.dirExists("/testdir", sudo = true)
        val res4 = prov.deleteDir("testdir", "/", true)
        val res5 = prov.dirExists("testdir", sudo = true)

        // then
        assertFalse(res1)
        assertTrue(res2.success)
        assertTrue(res3)
        assertTrue(res4.success)
        assertFalse(res5)
    }


    @Test
    fun userHome() {
        // given
        val prov = defaultTestContainer()

        // when
        val res1 = prov.userHome()

        // then
        assertEquals("/home/testuser/", res1)
    }


    @Test
    @ContainerTest
    fun replaceTextInFile() {
        // given
        val prov = defaultTestContainer()

        // when
        val file = "replaceTest"
        val res1 = prov.createFile(file, "a\nb\nc\nd")
        val res2 = prov.replaceTextInFile(file,"b", "hi\nho")
        val res3 = prov.fileContent(file).equals("a\nhi\nho\nc\nd")
        val res4 = prov.deleteFile(file)

        // then
        assertTrue(res1.success)
        assertTrue(res2.success)
        assertTrue(res3)
        assertTrue(res4.success)
    }


    @Test
    @ContainerTest
    fun replaceTextInFileRegex() {
        // given
        val prov = defaultTestContainer()

        // when
        val file = "replaceTest"
        val res1 = prov.createFile(file, "a\nbananas\nc\nd")
        val res2 = prov.replaceTextInFile(file, Regex("b.*n?nas\n"), "hi\nho\n")
        val res3 = prov.fileContent(file)
        val res4 = prov.deleteFile(file)

        // then
        assertTrue(res1.success)
        assertTrue(res2.success)
        assertEquals("a\nhi\nho\nc\nd",res3)
        assertTrue(res4.success)
    }


    @Test
    @ContainerTest
    fun insertTextInFile() {
        // given
        val prov = defaultTestContainer()

        // when
        val file = "insertTest"
        val res1 = prov.createFile(file, "a\nbananas\nc\nd")
        val res2 = prov.insertTextInFile(file, Regex("b.*n.nas\n"), "hi\n")
        val res3 = prov.fileContent(file)
        val res4 = prov.deleteFile(file)

        // then
        assertTrue(res1.success)
        assertTrue(res2.success)
        assertEquals("a\nbananas\nhi\nc\nd", res3)
        assertTrue(res4.success)
    }
}