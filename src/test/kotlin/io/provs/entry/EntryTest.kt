package io.provs.entry

import org.junit.jupiter.api.Test


@Suppress("unused")
fun testfun() {
    println("test is fun")
}


internal class EntryKtTest {

    @Test
    fun test_main_no_arg() {
        main("io.provs.entry.EntryTestKt", "testfun")
    }
}
