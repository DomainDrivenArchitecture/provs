package io.provs


open class Secret(private val value: String) {
    override fun toString(): String {
        return "********"
    }
    fun plain() : String {
        return  value
    }
}


class Password(plainPassword: String) : Secret(plainPassword)