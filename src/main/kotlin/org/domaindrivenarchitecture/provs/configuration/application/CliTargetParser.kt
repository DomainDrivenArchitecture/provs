package org.domaindrivenarchitecture.provs.configuration.application

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import org.domaindrivenarchitecture.provs.configuration.domain.TargetCliCommand

open class CliTargetParser(name: String) : ArgParser(name) {
    val target by argument(
        ArgType.String,
        description = "target: either 'local' or remote with 'user[:password]@host'",
    )
    val passwordInteractive by option(
        ArgType.Boolean,
        "password-interactive",
        "p",
        "prompt for password for remote target",
    ).default(false)
}

fun parseTarget(
    programName: String = "provs",
    args: Array<String>
): TargetCliCommand {
    val parser = CliTargetParser(programName)
    parser.parse(args)

    return TargetCliCommand(parser.target, parser.passwordInteractive)
}