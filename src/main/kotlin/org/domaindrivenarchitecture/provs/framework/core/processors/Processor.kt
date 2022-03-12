package org.domaindrivenarchitecture.provs.framework.core.processors


interface Processor {
    fun exec(vararg args: String): ProcessResult
    fun execNoLog(vararg args: String): ProcessResult
    fun close() {
        // no action needed for most processors; if action is needed when closing, this method must be overwritten in the subclass
    }
}


data class ProcessResult(val exitCode: Int, val out: String? = null, val err: String? = null, val ex: Exception? = null, val args: Array<out String> = emptyArray()) {

    private fun success(): Boolean {
        return (exitCode == 0)
    }

    fun argsToString() : String {
        return args.joinToString(
            separator = ", ",
            prefix = "[",
            postfix = "]",
            limit = 4,
            truncated = " ..."
        )
    }

    override fun toString(): String {
        return "--->>> ProcessResult: ${if (success()) "Succeeded" else "FAILED"} -- Code: $exitCode, ${if (!out.isNullOrEmpty()) "Out: $out, " else ""}${if (!err.isNullOrEmpty()) "Err: $err" else ""}" + argsToString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProcessResult

        if (exitCode != other.exitCode) return false
        if (out != other.out) return false
        if (err != other.err) return false
        if (ex != other.ex) return false
        if (!args.contentEquals(other.args)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = exitCode
        result = 31 * result + (out?.hashCode() ?: 0)
        result = 31 * result + (err?.hashCode() ?: 0)
        result = 31 * result + ex.hashCode()
        result = 31 * result + args.contentHashCode()
        return result
    }
}
