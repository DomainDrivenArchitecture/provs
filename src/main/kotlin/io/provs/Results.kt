package io.provs


data class ProvResult(val success: Boolean,
                        val cmd: String? = null,
                        val out: String? = null,
                        val err: String? = null,
                        val exception: Exception? = null,
                        val exit: String? = null) {

    constructor(returnCode : Int) : this(returnCode == 0)

    override fun toString(): String {
        return "ProvResult:: ${if (success) "Succeeded" else "FAILED"} -- ${if (!cmd.isNullOrEmpty()) "Name: " +
                cmd.escapeNewline() + ", " else ""}${if (!out.isNullOrEmpty()) "Details: $out" else ""}" +
                (exception?.run { " Exception: " + toString() } ?: "")
    }

    fun toShortString() : String {
        return "ProvResult:: ${if (success) "Succeeded" else "FAILED"} -- " +
                if (!success)
                        (if (out != null) "Details: $out " else "" +
                                if (err != null) " Error: " + err else "") else ""
    }
}


@Suppress("unused")  // might be used by custom methods
data class TypedResult<T>(val success: Boolean, val resultObject: T? = null) {
    override fun toString(): String {
        return "TypedResult:: ${if (success) "Succeeded" else "FAILED"} -- Result object: " + resultObject?.run { toString().escapeNewline() }
    }
}
