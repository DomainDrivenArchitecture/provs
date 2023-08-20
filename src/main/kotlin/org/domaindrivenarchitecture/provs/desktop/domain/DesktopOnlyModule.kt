package org.domaindrivenarchitecture.provs.desktop.domain

enum class DesktopOnlyModule {
    FIREFOX, VERIFY
    ;

    fun isIn(list: List<String>): Boolean {
        return list.any { it.equals(this.name, ignoreCase = true) }
    }
}