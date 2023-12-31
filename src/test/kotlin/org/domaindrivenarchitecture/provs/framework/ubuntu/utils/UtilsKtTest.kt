package org.domaindrivenarchitecture.provs.framework.ubuntu.utils

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.echoCommandForText
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class UtilsKtTest {

    // given
    val a = Prov.defaultInstance()

    @ContainerTest
    fun printToShell_escapes_String_successfully() {
        // when
        val testString = "test if newline \n and apostrophe's ' \" and special chars $ !§$%[]\\ äöüß \$variable and tabs     \t are handled correctly"

        val res = a.cmd(echoCommandForText(testString)).out

        // then
        assertEquals(testString, res)
    }

    @Test
    fun printToShell_escapes_raw_String_successfully() {
        // when
        val testMultiLineString = """
            test if newlines
            \n
            and apostrophe's ' " \" \' and special chars $ {} $\{something}!§$%[]\\ äöüß $\notakotlinvariable ${'$'}notakotlinvariable and tabs     \t are handled correctly
            """

        val resMl = a.cmd(echoCommandForText(testMultiLineString)).out

        // then
        assertEquals(testMultiLineString, resMl)
    }

    @Test
    fun printToShell_escapes_raw_String_from_function_successfully() {
        // when
        fun testMultiLineString() = """
            test if newlines
            \n
            and apostrophe's ' " \" \' and special chars $ {} $\{something}!§$%[]\\ äöüß $\notakotlinvariable ${'$'}notakotlinvariable and tabs     \t are handled correctly
            """

        val resMl = a.cmd(echoCommandForText(testMultiLineString())).out

        // then
        assertEquals(testMultiLineString(), resMl)
    }

    @ContainerTest
    fun echoCommandForText_in_ubuntu_container() {
        // given
        val prov = defaultTestContainer()

        // when
        val testMultiLineString = """
            test if newlines
            \n
            and apostrophe's ' " \" \' and special chars $ {} $\{something}!§$%[]\\ äöüß $\notakotlinvariable ${'$'}notakotlinvariable and tabs     \t are handled correctly
            """

        val resMl = prov.cmd(echoCommandForText(testMultiLineString)).out

        // then
        assertEquals(testMultiLineString, resMl)
    }
}