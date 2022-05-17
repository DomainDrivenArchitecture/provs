package org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
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
        val filename = "tmp/provstestfile123"
        val res2 = prov.createFile(filename, testtext)
        val textFromFile = prov.fileContent(filename)
        prov.deleteFile(filename)

        // then
        assertTrue(res2.success)
        assertEquals(testtext, textFromFile)
    }

    @ContainerTest
    fun createFile_successfully() {
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

    @ContainerTest
    fun createFile_with_dir_successfully() {
        // given
        val prov = defaultTestContainer()
        val filename = "dir1/dir2/testfile-with-dir"

        // when
        val res1 = prov.createFile(filename, testtext)
        val res2 = prov.createFile("sudo$filename", testtext, sudo = true)
        // check idempotence
        val res1b = prov.createFile(filename, testtext)
        val res2b = prov.createFile("sudo$filename", testtext, sudo = true)

        // then
        assertTrue(res1.success)
        assertTrue(prov.checkDir("dir1/dir2"))
        assertTrue(res1b.success)

        assertTrue(res2.success)
        assertTrue(prov.checkDir("sudodir1/dir2", sudo = true))
        assertTrue(res2b.success)
        assertEquals(testtext, prov.fileContent(filename))
        assertEquals(testtext, prov.fileContent("sudo$filename"))
    }

    @ContainerTest
    fun createFile_with_dir_fails_if_createDirIfMissing_is_false() {
        // given
        val prov = defaultTestContainer()
        val filename = "dirDoesNotExist/dir2/testfile-with-dir"

        // when
        val res = prov.createFile(filename, testtext, createDirIfMissing = false)
        val res2 = prov.createFile("sudo$filename", testtext, sudo = true, createDirIfMissing = false)

        // then
        assertFalse(res.success)
        assertFalse(res2.success)
        assertFalse(prov.checkDir("dirDoesNotExist"))
        assertFalse(prov.checkDir("sudodirDoesNotExist", sudo = true))
    }

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


    @ContainerTest
    fun create_and_delete_dir() {
        // given
        val prov = defaultTestContainer()

        // when
        val res1 = prov.checkDir("testdir")
        val res2 = prov.createDir("testdir", "~/")
        val res3 = prov.checkDir("testdir")
        val res4 = prov.deleteDir("testdir", "~/")
        val res5 = prov.checkDir("testdir")

        val res6 = prov.checkDir("testdir", "~/test")
        val res7 = prov.createDirs("test/testdir")
        val res8 = prov.checkDir("testdir", "~/test")
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


    @ContainerTest
    fun create_and_delete_dir_with_sudo() {
        // given
        val prov = defaultTestContainer()

        // when
        val res1 = prov.checkDir("/testdir", sudo = true)
        val res2 = prov.createDir("testdir", "/", sudo = true)
        val res3 = prov.checkDir("/testdir", sudo = true)
        val res4 = prov.deleteDir("testdir", "/", true)
        val res5 = prov.checkDir("testdir", sudo = true)

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

    @ContainerTest
    fun copyFileFromLocal_successfully() {
        // given
        val resourcesDirectory = File("src/test/resources").absolutePath

        // when
        defaultTestContainer().copyFileFromLocal("copiedFileFromLocal", "$resourcesDirectory/resource-test")

        // then
        val content = defaultTestContainer().fileContent("copiedFileFromLocal")
        assertEquals("resource text\n", content)
    }

    @ContainerTest
    fun fileContainsText() {
        // given
        val file = "file_with_text"
        defaultTestContainer().createFile(file, "\n\nabc\n- def\nefg\nhij\nklm\no\npq", sudo = true)

        // when
        val res = defaultTestContainer().fileContainsText(file, "abc")
        val res2 = defaultTestContainer().fileContainsText(file, "de")
        val res3 = defaultTestContainer().fileContainsText(file, "- def")
        val res4 = defaultTestContainer().fileContainsText(file, "xyy")
        val res5 = defaultTestContainer().fileContainsText(file, "c\n- def\nefg\nhi")
        val res6 = defaultTestContainer().fileContainsText(file, "\n\n")
        val res7 = defaultTestContainer().fileContainsText(file, "\n\n\n")
        val res8 = defaultTestContainer().fileContainsText(file, "\no\n")
        val res10 = defaultTestContainer().fileContainsText(file, "\n\nabc")

        // then
        assertTrue(res)
        assertTrue(res2)
        assertTrue(res3)
        assertFalse(res4)
        assertTrue(res5)
        assertTrue(res6)
        assertFalse(res7)
        assertTrue(res8)
        assertTrue(res10)
    }

    @ContainerTest
    fun fileContainsText_with_sudo() {
        // given
        val file = "sudotestfilecontainingtext"
        defaultTestContainer().createFile(file, "abc\n- def\nefg\nhij\nklm\nop", sudo = true)

        // when
        val res = defaultTestContainer().fileContainsText(file, "abc", sudo = true)
        val res2 = defaultTestContainer().fileContainsText(file, "de", sudo = true)
        val res3 = defaultTestContainer().fileContainsText(file, "- def", sudo = true)
        val res4 = defaultTestContainer().fileContainsText(file, "xyy", sudo = true)
        // test if newlines are recognized
        val res5 = defaultTestContainer().fileContainsText(file, "c\n- def\nefg\nhi", sudo = true)

        // then
        assertTrue(res)
        assertTrue(res2)
        assertTrue(res3)
        assertFalse(res4)
        assertTrue(res5)
    }

    @ExtensiveContainerTest
    fun fileContentLargeFile_success() {
        // given
        val prov = defaultTestContainer()
        val filename = "largetestfile"
        val content = "012345äöüß".repeat(100000)

        // when
        val res = prov.createFile(filename, content, overwriteIfExisting = true)
        val size = prov.fileSize(filename)
        val actualContent = prov.fileContentLargeFile(filename, chunkSize = 40000)

        // then
        assertTrue(res.success)
        assertEquals(content, actualContent)
        assertEquals(1400000, size)
    }

    @ExtensiveContainerTest
    fun fileContentLargeFile_with_sudo_success() {
        // given
        val prov = defaultTestContainer()
        val filename = "largetestfile"
        val content = "012345äöüß".repeat(100000)

        // when
        val res = prov.createFile(filename, content, overwriteIfExisting = true, sudo = true)
        val size = prov.fileSize(filename, sudo = true)
        val actualContent = prov.fileContentLargeFile(filename, chunkSize = 40000, sudo = true)

        // then
        assertTrue(res.success)
        assertEquals(content, actualContent)
        assertEquals(1400000, size)
    }

    @ContainerTest
    fun test_createParentDir() {
        // given
        val prov = defaultTestContainer()
        val filename = "parent_dir/test/file"

        // when
        val res = prov.createParentDir(File(filename))
        val dirExists = prov.checkDir("parent_dir/test")
        val res2 = prov.createParentDir(File(filename))  // test idempotence
        val dirExists2 = prov.checkDir("parent_dir/test")

        // then
        assertTrue(res.success)
        assertTrue(dirExists)
        assertTrue(res2.success)
        assertTrue(dirExists2)

    }

    @ContainerTest
    fun test_createLink_without_dir() {
        // given
        val prov = defaultTestContainer()
        val source = File("testoriginalfile")
        val target = File("testlink")
        prov.createFile(source.toString(), "textinlinkfile")

        // when
        val res = prov.createSymlink(source, target)
        val res2 = prov.createSymlink(source, target)   // test idempotence
        val linkExists = prov.checkFile(target.name)
        val content = prov.fileContent(target.name)

        // then
        assertTrue(res.success)
        assertTrue(res2.success)
        assertTrue(linkExists)
        assertEquals("textinlinkfile", content)
    }

    @ContainerTest
    fun test_createLink_with_dirs() {
        // given
        val prov = defaultTestContainer()
        val source = File("~/linkoriginalfiledir/testoriginalfile2")
        val target = File("linkdir1/linkdir2/testlink2")
        prov.createFile(source.toString(), "textinlinkfile2")

        // when
        val res = prov.createSymlink(source, target)
        val res2 = prov.createSymlink(source, target)   // test idempotence
        val linkExists = prov.checkFile(target.toString())
        val content = prov.fileContent(target.toString())

        // then
        assertTrue(res.success)
        assertTrue(res2.success)
        assertTrue(linkExists)
        assertEquals("textinlinkfile2", content)
    }

    @ContainerTest
    fun test_createLink_with_dirs_and_sudo() {
        // given
        val prov = defaultTestContainer()
        val source = File("~/linkoriginalfiledirsudo/testoriginalfilesudo")
        val target = File("linkdir1sudo/linkdir2sudo/testlinksudo")
        prov.createFile(source.toString(), "textinlinkfilesudo", sudo = true)

        // when
        val res = prov.createSymlink(source, target, sudo = true)
        val res2 = prov.createSymlink(source, target, sudo = true)   // test idempotence
        val linkExists = prov.checkFile(target.toString(), sudo = true)
        val content = prov.fileContent(target.toString(), sudo = true)

        // then
        assertTrue(res.success)
        assertTrue(res2.success)
        assertTrue(linkExists)
        assertEquals("textinlinkfilesudo", content)
    }
}
