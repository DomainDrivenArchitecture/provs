package org.domaindrivenarchitecture.provs.desktop.domain

import org.domaindrivenarchitecture.provs.desktop.domain.DesktopType.Companion.BASIC
import org.domaindrivenarchitecture.provs.desktop.domain.DesktopType.Companion.IDE
import org.domaindrivenarchitecture.provs.desktop.domain.SubDesktopType.Companion.SUBTYPE
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

// tests subclassing of DesktopType
internal open class SubDesktopType protected constructor(name: String) : DesktopType(name) {

    companion object {

        // defines a new DesktopType
        val SUBTYPE = SubDesktopType("SUBTYPE")

        private val values = DesktopType.values + SUBTYPE

        fun valueOf(value: String): DesktopType {
            return valueOf(value, values)
        }
    }
}

internal class DesktopTypeTest {

    @Test
    fun test_valueOf() {
        assertEquals(BASIC, DesktopType.valueOf("basic"))
        assertEquals(BASIC, DesktopType.valueOf("Basic"))
        assertEquals(IDE, DesktopType.valueOf("IDE"))

        val exception = assertThrows(RuntimeException::class.java) {
            DesktopType.valueOf("subtype")
        }
        assertEquals("No DesktopType found for value: subtype", exception.message)
    }

    @Test
    fun test_valueOf_in_subclass() {
        assertEquals(SUBTYPE, SubDesktopType.valueOf("subtype"))
        assertEquals(BASIC, SubDesktopType.valueOf("basic"))
        assertNotEquals(SUBTYPE, DesktopType.valueOf("basic"))

        val exception = assertThrows(RuntimeException::class.java) {
            DesktopType.valueOf("subtype2")
        }
        assertEquals("No DesktopType found for value: subtype2", exception.message)
    }
}
