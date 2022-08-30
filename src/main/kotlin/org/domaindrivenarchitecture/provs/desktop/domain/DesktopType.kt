package org.domaindrivenarchitecture.provs.desktop.domain


/**
 * Provides desktop types. For each type a different set of software and packages is installed, see README.md.
 */
// Uses a regular class instead of an enum class in order to allow subclasses which can add new DesktopTypes
open class DesktopType protected constructor(val name: String) {

    companion object {

        val BASIC = DesktopType("BASIC")
        val OFFICE = DesktopType("OFFICE")
        val IDE = DesktopType("IDE")

        @JvmStatic
        protected val values = listOf(BASIC, OFFICE, IDE)

        @JvmStatic
        fun valueOf(value: String): DesktopType = valueOf(value, values)

        @JvmStatic
        protected fun valueOf(value: String, valueList: List<DesktopType>): DesktopType {
            for (type in valueList) {
                if (value.uppercase().equals(type.name)) {
                    return type
                }
            }
            throw RuntimeException("No DesktopType found for value: $value")
        }
    }

    override fun toString(): String {
        return name
    }
}
