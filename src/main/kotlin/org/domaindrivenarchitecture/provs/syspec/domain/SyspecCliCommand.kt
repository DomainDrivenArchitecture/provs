package org.domaindrivenarchitecture.provs.syspec.domain

import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.configuration.domain.TargetCliCommand

class SyspecCliCommand (
    val target: TargetCliCommand,
    val configFileName: ConfigFileName?,
)