package io.provs.entry

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream


@Suppress("unused")
fun testfun(args: Array<String>) {
    println("test is fun " + args.joinToString(" "))
}

@Suppress("unused")
fun main(args: Array<String>) {
    println("main is fun " + args.joinToString(" "))
}


internal class EntryKtTest {

    private var outContent = ByteArrayOutputStream()
    private var originalOut = System.out

    @BeforeEach
    fun redirectSystemOutStream() {
        originalOut = System.out

        // given
        outContent = ByteArrayOutputStream()
        System.setOut(PrintStream(outContent))
    }

    @AfterEach
    fun restoreSystemOutStream() {
        System.setOut(originalOut)
    }

    @Test
    fun test_without_method_argument() {
        // when
        main("io.provs.entry.EntryTestKt")

        // then
        assertEquals("main is fun \n", outContent.toString())
    }

    @Test
    fun test_method_main_without_args() {
        // when
        main("io.provs.entry.EntryTestKt", "main")

        // then
        assertEquals("main is fun \n", outContent.toString())
    }

    @Test
    fun test_named_method_without_args() {
        // when
        main("io.provs.entry.EntryTestKt", "testfun")

        // then
        assertEquals("test is fun \n", outContent.toString())
    }

    @Test
    fun test_method_main_with_args() {
        // when
        main("io.provs.entry.EntryTestKt", "main", "arg1", "arg2")

        // then
        assertEquals("main is fun arg1 arg2\n", outContent.toString())
    }

    @Test
    fun test_named_method_with_args() {
        // when
        main("io.provs.entry.EntryTestKt", "testfun", "arg1", "arg2")

        // then
        assertEquals("test is fun arg1 arg2\n", outContent.toString())
    }
}
