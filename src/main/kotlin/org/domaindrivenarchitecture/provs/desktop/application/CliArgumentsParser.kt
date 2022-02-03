package org.domaindrivenarchitecture.provs.desktop.application

import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.multiple
import org.domaindrivenarchitecture.provs.configuration.application.CliTargetParser
import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.configuration.domain.TargetCliCommand
import org.domaindrivenarchitecture.provs.desktop.domain.Scope


open class CliArgumentsParser(name: String) : CliTargetParser(name) {

    val configFileName by argument(
        ArgType.String,
        "configFilename",
        "the filename containing the yaml config for the desktop"
    )

    val scopes by option (
        type = ArgType.Choice<Scope>(),
        shortName = "s",
        fullName = "scope",
        description = "only provision component in scope."
    ).multiple()

    fun parseWorkplaceArguments(args: Array<String>): DesktopCliCommand {
        super.parse(args)

        return DesktopCliCommand(
            ConfigFileName(configFileName),
            scopes,
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