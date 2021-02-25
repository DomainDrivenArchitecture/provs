package io.provs

import io.provs.testconfig.tags.CONTAINERTEST
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

internal class UtilsTest {

    @Test
    fun test_getCallingMethodName() {
        // when
        val s = getCallingMethodName()

        // then
        Assertions.assertEquals("test_getCallingMethodName", s)
    }

    @Test
    @Tag(CONTAINERTEST)
    fun test_docker() {
        // when
        val res = defaultTestContainer().cmd("echo something")

        // then
        Assertions.assertEquals(true, res.success)
    }
}