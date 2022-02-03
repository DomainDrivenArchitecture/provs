package org.domaindrivenarchitecture.provs.desktop.application

import kotlinx.cli.ArgType
import org.domaindrivenarchitecture.provs.configuration.application.CliTargetParser
import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.configuration.domain.TargetCliCommand


open class CliArgumentsParser(name: String) : CliTargetParser(name) {

    val configFileName by argument(
        ArgType.String,
        "configFilename",
        "the filename containing the yaml config for the desktop"
    )


    fun parseWorkplaceArguments(args: Array<String>): DesktopCliCommand {
        super.parse(args)

        return DesktopCliCommand(
            ConfigFileName(configFileName),
            TargetCliCommand(
                localHost,
                remoteHost,
                userName,
                sshWithPasswordPrompt,
                sshWithGopassPath,
                sshWithKey
            )
        )
    }
}