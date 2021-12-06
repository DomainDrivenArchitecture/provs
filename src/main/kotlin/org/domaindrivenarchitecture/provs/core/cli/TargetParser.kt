package org.domaindrivenarchitecture.provs.core.cli

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.Secret
import org.domaindrivenarchitecture.provs.core.local
import org.domaindrivenarchitecture.provs.core.remote
import org.domaindrivenarchitecture.provs.ubuntu.secret.secretSources.GopassSecretSource
import org.domaindrivenarchitecture.provs.ubuntu.secret.secretSources.PromptSecretSource
import org.domaindrivenarchitecture.provs.ubuntu.user.base.currentUserCanSudo
import org.domaindrivenarchitecture.provs.ubuntu.user.base.makeUserSudoerWithNoSudoPasswordRequired
import org.domaindrivenarchitecture.provs.ubuntu.user.base.whoami
import kotlin.system.exitProcess

open class TargetParser(name: String) : ArgParser(name) {
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
