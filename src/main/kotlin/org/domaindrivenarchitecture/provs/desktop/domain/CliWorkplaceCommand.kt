package org.domaindrivenarchitecture.provs.desktop.application

import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.configuration.domain.TargetCliCommand


class DesktopCliCommand(val configFile: ConfigFileName, val target: TargetCliCommand) {

    fun isValid(): Boolean {
        return configFile.fileName.isNotEmpty() && target.isValid()
    }
}

