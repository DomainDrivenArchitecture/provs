package org.domaindrivenarchitecture.provs.configuration.application

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default

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