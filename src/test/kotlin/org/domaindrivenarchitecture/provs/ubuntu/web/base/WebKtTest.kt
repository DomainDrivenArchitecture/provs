package org.domaindrivenarchitecture.provs.ubuntu.web.base

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.createFile
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.fileContent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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

        // then
        val res2 = a.fileContent("/tmp/file2")

        assertTrue(res.success)
        assertEquals("hello", res2)
    }
}