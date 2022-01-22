package org.domaindrivenarchitecture.provs.framework.core

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.net.UnknownHostException

internal class UtilsKtTest {

    @Test
    fun test_getCallingMethodName() {
        // when
        val s = getCallingMethodName()

        // then
        assertEquals("test_getCallingMethodName", s)
    }

    @Test
    @ContainerTest
    fun runCmdInContainer() {
        // when
        val res = defaultTestContainer().cmd("echo something")

        // then
        assertTrue(res.success)
    }

    @Test
    fun remote_emptyHost() {
        assertThrows(IllegalArgumentException::class.java,
            { remote("", "user") })
    }

    @Test
    fun remote_invalidHost() {
        assertThrows(
            UnknownHostException::class.java,
            { remote("invalid_host", "user") })
    }

    @Test
    fun getResourceAsText_successful() {
        assertEquals("resource text\n", getResourceAsText("resource-test"))
    }

    @Test
    fun getResourceAsText_throws_exception_for_missing_file() {
        assertThrows<IllegalArgumentException> {
            getResourceAsText("not existing resource")
        }
    }

    @Test
    fun getLocalFileContent_successful() {
        val resourcesDirectory = File("src/test/resources").absolutePath
        assertEquals("resource text\n", getLocalFileContent("$resourcesDirectory/resource-test"))
    }

    @Test
    @Disabled // run manually after having updated user
    fun test_remote() {
        assertTrue(remote("127.0.0.1", "user").cmd("echo sth").success)
    }

    @Test
    fun test_resolveTemplate_successfully() {
        // given
        val DOUBLE_ESCAPED_DOLLAR = "\${'\${'\$'}'}"
        val input = """
            line1
            line2: ${'$'}var1
              line3
                line4=${'$'}var2
              line5 with 3 dollars ${'$'}${'$'}${'$'}
              line6${'$'}{var3}withpostfix
              line7 with double escaped dollars ${DOUBLE_ESCAPED_DOLLAR}
                """.trimIndent()

        // when
        val res = input.resolve(values = mapOf("var1" to "VALUE1", "var2" to "VALUE2", "var3" to "VALUE3"))

        // then
        val ESCAPED_DOLLAR = "\${'\$'}"
        val RESULT = """
            line1
            line2: VALUE1
              line3
                line4=VALUE2
              line5 with 3 dollars ${'$'}${'$'}${'$'}
              line6VALUE3withpostfix
              line7 with double escaped dollars $ESCAPED_DOLLAR
                """.trimIndent()

        assertEquals(RESULT, res)
    }


    @Test
    fun test_resolveTemplate_with_invalid_data_throws_exception() {
        // given
        val DOUBLE_ESCAPED_DOLLAR = "\${'\${'\$'}'}"
        val input = """
            line1
            line2: ${'$'}var1
              line3
                line4=${'$'}var2
              line5 with 3 dollars ${'$'}${'$'}${'$'}
              line6${'$'}{var3}withpostfix
              line7 with double escaped dollars ${DOUBLE_ESCAPED_DOLLAR}
                """.trimIndent()

        // when
        val e = assertThrows(IllegalArgumentException::class.java) {
            input.resolve(values = mapOf("var1" to "VALUE1", "var2" to "VALUE2", "wrongkey" to "VALUE3"))
        }

        // then
        assertEquals(e.message, "No value found for: var3")
    }
}