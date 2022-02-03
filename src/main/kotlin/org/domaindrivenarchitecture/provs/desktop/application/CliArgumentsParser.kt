package org.domaindrivenarchitecture.provs.desktop.application

import kotlinx.cli.ArgType
import kotlinx.cli.optional
import org.domaindrivenarchitecture.provs.framework.core.cli.CliTargetParser
import org.domaindrivenarchitecture.provs.framework.core.cli.TargetCliCommand


open class CliArgumentsParser(name: String) : CliTargetParser(name) {

    val configFileName by argument(
        ArgType.String,
        "configFilename",
        "the filename containing the yaml config for the workplace"
    ).optional()


    fun parseWorkplaceArguments(args: Array<String>): DesktopCliCommand {
        super.parse(args)

        return DesktopCliCommand(
            configFileName ?: "WorkplaceConfig.yaml",
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