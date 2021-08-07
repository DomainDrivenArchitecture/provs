package io.provs.ubuntu.web.base

import io.provs.test.defaultTestContainer
import io.provs.test.tags.ContainerTest
import io.provs.ubuntu.filesystem.base.createFile
import io.provs.ubuntu.filesystem.base.fileContent
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