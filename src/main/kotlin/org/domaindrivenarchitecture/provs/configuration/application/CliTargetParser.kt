package org.domaindrivenarchitecture.provs.configuration.application

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import org.domaindrivenarchitecture.provs.configuration.domain.TargetCliCommand

open class CliTargetParser(name: String) : ArgParser(name) {
    val remoteHost by option(
        ArgType.String, shortName =
        "r", description = "provision to remote host - either localHost or remoteHost must be specified"
    )
    val localHost by option(
        ArgType.Boolean, shortName =
        "l", description = "provision to local machine - either localHost or remoteHost must be specified"
    )
    val userName by option(
        ArgType.String,
        shortName = "u",
        description = "user for remote provisioning."
    )
    val sshWithGopassPath by option(
        ArgType.String,
        shortName = "p",
        description = "password stored at gopass path"
    )
    val sshWithPasswordPrompt by option(
        ArgType.Boolean,
        shortName = "i",
        description = "prompt for password interactive"
    ).default(false)
    val sshWithKey by option(
        ArgType.Boolean,
        shortName = "k",
        description = "provision over ssh using user & ssh key"
    ).default(false)
}

fun parseTarget(
    programName: String = "java -jar provs.jar",
    args: Array<String>
): TargetCliCommand {
    val parser = CliTargetParser(programName)
    parser.parse(args)

    return TargetCliCommand(
        parser.localHost,
        parser.remoteHost,
        parser.userName,
        parser.sshWithPasswordPrompt,
        parser.sshWithGopassPath,
        parser.sshWithKey
    )
}