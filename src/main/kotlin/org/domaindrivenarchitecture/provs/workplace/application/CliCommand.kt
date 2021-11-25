package org.domaindrivenarchitecture.provs.workplace.application

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.optional

class CliCommand(
    val remoteHost: String?,
    val localHost: Boolean?,
    val userName: String?,
    val sshWithGopassPath: String?,
    val sshWithPasswordPrompt: Boolean,
    val sshWithKey: Boolean,
    _configFileName: String?
) {
    val configFileName: String

    init {
        configFileName = _configFileName ?: "WorkplaceConfig.yaml"
    }

    fun isValidLocalhost(): Boolean {
        return (localHost ?: false) && remoteHost == null && userName == null && sshWithGopassPath == null &&
                !sshWithPasswordPrompt && !sshWithKey
    }

    fun hasValidPasswordOption(): Boolean {
        return (sshWithGopassPath != null) xor sshWithPasswordPrompt xor sshWithKey
    }

    fun isValidRemote(): Boolean {
        return remoteHost != null && userName != null && hasValidPasswordOption()
    }

    fun isValid(): Boolean {
        return (isValidLocalhost() || isValidRemote())
    }
}

fun parseCli(args: Array<String>): CliCommand {
    val parser = ArgParser("java -jar provs.jar")

    val remoteHost by parser.option(
        ArgType.String, shortName =
        "r", description = "provision to remote host - either localHost or remoteHost must be specified"
    )
    val localHost by parser.option(
        ArgType.Boolean, shortName =
        "l", description = "provision to local machine - either localHost or remoteHost must be specified"
    )
    val configFileName by parser.option(
        ArgType.String,
        shortName = "c",
        description = "the config file name to apply"
    ).default("WorkplaceConfig.yaml")
    val userName by parser.option(
        ArgType.String,
        shortName = "u",
        description = "user for remote provisioning."
    )
    val sshWithGopassPath by parser.option(
        ArgType.String,
        shortName = "p",
        description = "password stored at gopass path"
    )
    val sshWithPasswordPrompt by parser.option(
        ArgType.Boolean,
        shortName = "i",
        description = "prompt for password interactive"
    ).default(false)
    val sshWithKey by parser.option(
        ArgType.Boolean,
        shortName = "k",
        description = "provision over ssh using user & ssh key"
    ).default(false)
    parser.parse(args)
    val cliCommand =
        CliCommand(
            remoteHost, localHost, userName, sshWithGopassPath, sshWithPasswordPrompt, sshWithKey, configFileName
        )
    return cliCommand
}
