package org.domaindrivenarchitecture.provs.desktop.application

import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.configuration.domain.TargetCliCommand
import org.domaindrivenarchitecture.provs.desktop.domain.Scope


class DesktopCliCommand(
    val configFile: ConfigFileName,
    val scopes: List<Scope>,
    val target: TargetCliCommand, ) {

    fun isValid(): Boolean {
        return configFile.fileName.isNotEmpty() && target.isValid()
    }

    fun haScope(): Boolean {
        return scopes.isNotEmpty()
    }
}

