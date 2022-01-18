package org.domaindrivenarchitecture.provs.workplace.application

import kotlinx.cli.ArgType
import kotlinx.cli.optional
import org.domaindrivenarchitecture.provs.framework.core.cli.CliTargetParser
import org.domaindrivenarchitecture.provs.framework.core.cli.TargetCliCommand


open class CliWorkplaceParser(name: String) : CliTargetParser(name) {

    val configFileName by argument(
        ArgType.String,
        "configFilename",
        "the filename containing the yaml config for the workplace"
    ).optional()


    fun parseWorkplaceArguments(args: Array<String>): WorkplaceCliCommand {
        super.parse(args)

        return WorkplaceCliCommand(
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