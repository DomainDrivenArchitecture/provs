package org.domaindrivenarchitecture.provs.desktop.application

import org.domaindrivenarchitecture.provs.framework.core.cli.TargetCliCommand


class WorkplaceCliCommand(val configFile: String, val target: TargetCliCommand) {

    fun isValid(): Boolean {
        return configFile.isNotEmpty() && target.isValid()
    }
}
