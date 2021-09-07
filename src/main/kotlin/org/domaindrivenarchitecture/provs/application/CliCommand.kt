package org.domaindrivenarchitecture.provs.application

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.optional

class CliCommand(
    val remoteHost: String?,
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
        return remoteHost == null && userName == null && sshWithGopassPath == null &&
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
    val parser = ArgParser("meissa.provs.application.CliKt main")

    val configFileName by parser.argument(ArgType.String, description = "the config file name to apply").optional()

    val remoteHost by parser.option(
        ArgType.String, shortName =
        "r", description = "provision remote host"
    )
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
            remoteHost, userName, sshWithGopassPath, sshWithPasswordPrompt, sshWithKey, configFileName
        )
    return cliCommand
}
