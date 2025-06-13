package org.domaindrivenarchitecture.provs.framework.core

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ProvCommandEscapingTest {

    // given
    val defaultProv = Prov.defaultInstance()

    @ContainerTest
    fun printToShell_escapes_String_successfully() {
        // given
        val testString = "test if newline \n and apostrophe's ' \" and special chars $ !§$%[]\\ äöüß \$variable and tabs     \t are handled correctly"

        // when
        val res = defaultProv.cmd(echoCommandForText(testString)).out

        // then
        Assertions.assertEquals(testString, res)
    }

    @Test
    fun printToShell_escapes_raw_String_successfully() {
        // given
        val testMultiLineString = """
            test if newlines
            \n
            and apostrophe's ' " \" \' and special chars $ {} $\{something}!§$%[]\\ äöüß $\notakotlinvariable ${'$'}notakotlinvariable and tabs     \t are handled correctly
            """
        // when
        val resMl = defaultProv.cmd(echoCommandForText(testMultiLineString)).out

        // then
        Assertions.assertEquals(testMultiLineString, resMl)
    }

    @Test
    fun printToShell_escapes_raw_String_from_function_successfully() {
        // given
        fun testMultiLineString() = """
            test if newlines
            \n
            and apostrophe's ' " \" \' and special chars $ {} $\{something}!§$%[]\\ äöüß $\notakotlinvariable ${'$'}notakotlinvariable and tabs     \t are handled correctly
            """

        // when
        val resMl = defaultProv.cmd(echoCommandForText(testMultiLineString())).out

        // then
        Assertions.assertEquals(testMultiLineString(), resMl)
    }

    @ContainerTest
    fun echoCommandForText_in_ubuntu_container() {
        // given
        val prov = defaultTestContainer()

        val testMultiLineString = """
            test if newlines
            \n
            and apostrophe's ' " \" \' and special chars $ {} $\{something}!§$%[]\\ äöüß $\notakotlinvariable ${'$'}notakotlinvariable and tabs     \t are handled correctly
            """

        // when
        val resMl = prov.cmd(echoCommandForText(testMultiLineString)).out

        // then
        Assertions.assertEquals(testMultiLineString, resMl)
    }
}