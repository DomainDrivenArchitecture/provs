package org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.test.testLocal
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File


internal class FilesystemKtTest {

    val testtext = "tabs \t\t\t triple quotes \"\"\"" + """
    newline
    and apostrophe's ' '' ''' \' " "" \" and special chars ${'$'} {} ${'$'}\{something}<>äöüß!§@€%#|&/[]\\ äöüß %s %% %%% \\ \\\ \\\\ \\\\\ ${'$'}\notakotlinvariable ${'$'}notakotlinvariable and tabs 	 and \t should be handled correctly
    """

    @Test
    fun createFile_locally() {
        // given
        val prov = testLocal()
        prov.createDir("tmp")

        // when
        val filename = "tmp/testfile9"
        val res2 = prov.createFile(filename, testtext)
        val textFromFile = prov.fileContent(filename)
        prov.deleteFile(filename)

        // then
        assertTrue(res2.success)
        assertEquals(testtext, textFromFile)
    }

    @Test
    @ContainerTest
    fun createFile_in_container() {
        // given
        val prov = defaultTestContainer()
        val filename = "testfile8"

        // when
        val res = prov.createFile(filename, testtext)
        val res2 = prov.createFile("sudo$filename", testtext, sudo = true)

        // then
        assertTrue(res.success)
        assertTrue(res2.success)
        assertEquals(testtext, prov.fileContent(filename))
        assertEquals(testtext, prov.fileContent("sudo$filename"))
    }

    @Test
    @ContainerTest
    fun create_large_file_in_container() {
        // given
        val prov = defaultTestContainer()
        val filename = "largetestfile"
        val content = "012345äöüß".repeat(100000)

        // when
        val res = prov.createFile(filename, content)
        val size = prov.fileSize(filename)

        // then
        assertTrue(res.success)
        assertEquals(1400000, size)
        // assertEquals(testtext, prov.fileContent(filename))
    }

    @Test
    @ContainerTest
    fun create_and_delete_file() {
        // given
        val prov = defaultTestContainer()

        // when
        val res1 = prov.checkFile("testfile")
        val res2 = prov.createFile("testfile", "some content")
        val res3 = prov.checkFile("testfile")
        val res4a = prov.fileContainsText("testfile", "some content")
        val res4b = prov.fileContainsText("testfile", "some non-existing content")
        val res5 = prov.deleteFile("testfile")
        val res6 = prov.checkFile("testfile")
        val res7 = prov.deleteFile("testfile")  // idem-potent

        // then
        assertFalse(res1)
        assertTrue(res2.success)
        assertTrue(res3)
        assertTrue(res4a)
        assertFalse(res4b)
        assertTrue(res5.success)
        assertFalse(res6)
        assertTrue(res7.success)
    }


    @Test
    @ContainerTest
    fun create_and_delete_file_with_sudo() {
        // given
        val prov = defaultTestContainer()

        // when
        val file = "/testfile"
        val res1 = prov.checkFile(file)
        val res2 = prov.createFile(file, "some content", sudo = true)
        val res3 = prov.checkFile(file)
        val res4a = prov.fileContainsText(file, "some content")
        val res4b = prov.fileContainsText(file, "some non-existing content")
        val res5 = prov.deleteFile(file)
        val res6 = prov.checkFile(file)
        val res7 = prov.deleteFile(file, sudo = true)
        val res8 = prov.checkFile(file)
        val res9 = prov.deleteFile(file, sudo = true)  // check idem-potence

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
        assertTrue(res9.success)
    }


    @Test
    @ContainerTest
    fun create_and_delete_dir() {
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
    fun create_and_delete_dir_with_sudo() {
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
    @ContainerTest
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
        val res2 = prov.replaceTextInFile(file, "b", "hi\nho")
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
        assertEquals("a\nhi\nho\nc\nd", res3)
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

    @Test
    @ContainerTest
    fun copyFileFromLocal_successfully() {
        // given
        val resourcesDirectory = File("src/test/resources").absolutePath

        // when
        defaultTestContainer().copyFileFromLocal("copiedFileFromLocal", "$resourcesDirectory/resource-test")

        // then
        val content = defaultTestContainer().fileContent( "copiedFileFromLocal")
        assertEquals("resource text\n", content)
    }

    @Test
    @ContainerTest
    fun fileContainsText() {
        // given
        defaultTestContainer().createFile("testfilecontainingtext", "abc\n- def\nefg")

        // when
        val res = defaultTestContainer().fileContainsText("testfilecontainingtext", "abc")
        val res2 = defaultTestContainer().fileContainsText("testfilecontainingtext", "de")
        val res3 = defaultTestContainer().fileContainsText("testfilecontainingtext", "- def")
        val res4 = defaultTestContainer().fileContainsText("testfilecontainingtext", "xyy")

        // then
        assertTrue(res)
        assertTrue(res2)
        assertTrue(res3)
        assertFalse(res4)
    }
}
