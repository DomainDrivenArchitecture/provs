package org.domaindrivenarchitecture.provs.framework.core

import java.util.*


open class Secret(private val value: String) {
    override fun toString(): String {
        return "********"
    }
    fun plain() : String {
        return  value
    }
    override fun equals(other: Any?): Boolean {
        return (this === other) || ((other is Secret) && (this.value == other.value))
    }
    override fun hashCode(): Int {
        return Objects.hash(value)
    }
}
