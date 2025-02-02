package org.domaindrivenarchitecture.provs.framework.core


data class ProvResult(
    val success: Boolean,
    val cmd: String? = null,
    val out: String? = null,
    val err: String? = null,
    val exception: Exception? = null,
    val exit: String? = null,
    val info: String? = null,
) {

    val outTrimmed: String? = out?.trim()

    constructor(returnCode: Int) : this(returnCode == 0)

    override fun toString(): String {

        return "ProvResult:: ${if (success) "Succeeded" else "FAILED"} -- ${
            if (!cmd.isNullOrEmpty()) "Name: " +
                    cmd.escapeNewline() + ", " else ""
        }${if (!out.isNullOrEmpty()) "Details: $out" else ""}" +
                (exception?.run { " Exception: " + toString() } ?: "")
    }
}
