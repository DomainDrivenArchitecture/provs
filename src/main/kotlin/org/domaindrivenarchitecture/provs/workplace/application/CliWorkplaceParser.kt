package org.domaindrivenarchitecture.provs.workplace.application

import kotlinx.cli.ArgType
import kotlinx.cli.optional
import org.domaindrivenarchitecture.provs.core.cli.CliTargetParser

class CliWorkplaceParser(name: String) : CliTargetParser(name) {
    val configFileName by argument(
        ArgType.String,
        "configFilename",
        "the filename containing the yaml config for the workplace"
    ).optional()
}