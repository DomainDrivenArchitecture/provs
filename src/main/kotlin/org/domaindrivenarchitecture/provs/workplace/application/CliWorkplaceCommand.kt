package org.domaindrivenarchitecture.provs.workplace.application

import org.domaindrivenarchitecture.provs.core.cli.TargetCliCommand


class WorkplaceCliCommand(val configFile: String, val target: TargetCliCommand) {

    fun isValid(): Boolean {
        return configFile.isNotEmpty() && target.isValid()
    }
}

