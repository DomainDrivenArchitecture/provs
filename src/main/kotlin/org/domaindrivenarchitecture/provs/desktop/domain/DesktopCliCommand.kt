package org.domaindrivenarchitecture.provs.desktop.domain

import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.configuration.domain.TargetCliCommand


class DesktopCliCommand(
    val type: DesktopType,
    val target: TargetCliCommand,
    val configFile: ConfigFileName?,
    val onlyModules: List<String>? = null
) {
    fun isValid(): Boolean {
        return target.isValid()
    }
}
