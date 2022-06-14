package org.domaindrivenarchitecture.provs.desktop.domain


/**
 * Provides desktop types. For each type a different set of software and packages is installed, see README.md.
 */
open class DesktopType(val name: String) {

    // A regular class is used rather than enum class in order to allow extending DesktopType by subclassing.

    companion object {

        val BASIC = DesktopType("BASIC")
        val OFFICE = DesktopType("OFFICE")
        val IDE = DesktopType("IDE")

        @JvmStatic
        protected val values = listOf(BASIC, OFFICE, IDE)

        fun valueOf(value: String): DesktopType {
            return valueOf(value, values)
        }
        fun valueOf(value: String, v: List<DesktopType>): DesktopType {
            for (t in v) {
                if (value.uppercase().equals(t.name)) {
                    return t
                }
            }
            throw RuntimeException("No DesktopType found for value: $value")
        }
    }

    override fun toString(): String {
        return name
    }
}

