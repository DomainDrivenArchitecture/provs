package org.domaindrivenarchitecture.provs.syspec.application

import kotlinx.cli.ArgType
import kotlinx.cli.default
import org.domaindrivenarchitecture.provs.configuration.application.CliTargetParser
import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.configuration.domain.TargetCliCommand
import org.domaindrivenarchitecture.provs.syspec.domain.SyspecCliCommand

class CliArgumentsParser(name: String) : CliTargetParser(name) {

    val cliConfigFileName by option(
        ArgType.String,
        "config-file",
        "c",
        "the filename containing the yaml config"
    ).default("syspec-config.yaml")

    fun parseCommand(args: Array<String>): SyspecCliCommand {
        super.parse(args)

        return SyspecCliCommand(
            TargetCliCommand(
                target,
                passwordInteractive
            ),
            ConfigFileName(cliConfigFileName)
        )
    }
}

