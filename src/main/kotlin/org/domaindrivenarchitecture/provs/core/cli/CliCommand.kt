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


class CliCommand(
    val localHost: Boolean?,
    val remoteHost: String?,
    val userName: String?,
    val sshWithPasswordPrompt: Boolean,
    val sshWithGopassPath: String?,
    val sshWithKey: Boolean
) {
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

fun parseCli(
    programName: String = "java -jar provs.jar",
    args: Array<String>
): CliCommand {
    val parser = ArgParser(programName)

    val remoteHost by parser.option(
        ArgType.String, shortName =
        "r", description = "provision to remote host - either localHost or remoteHost must be specified"
    )
    val localHost by parser.option(
        ArgType.Boolean, shortName =
        "l", description = "provision to local machine - either localHost or remoteHost must be specified"
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

    return CliCommand(
        localHost, remoteHost, userName, sshWithPasswordPrompt, sshWithGopassPath, sshWithKey
    )
}


internal fun createProvInstance(
    cliCommand: CliCommand,
    remoteHostSetSudoWithoutPasswordRequired: Boolean = false
): Prov {
    if (cliCommand.isValid()) {
        val password: Secret? = if (cliCommand.isValidRemote()) retrievePassword(cliCommand) else null

        if (cliCommand.isValidLocalhost()) {
            return local()
        } else if (cliCommand.isValidRemote()) {
            val host = cliCommand.remoteHost!!
            val remoteUser = cliCommand.userName!!

            val prov =
                if (cliCommand.sshWithKey) {
                    remote(host, remoteUser)
                } else {
                    require(
                        password != null,
                        { "No password available for provisioning without ssh keys. Either specify provisioning by ssh-keys or provide password." })
                    remote(host, remoteUser, password)
                }

            if (!prov.currentUserCanSudo()) {
                if (remoteHostSetSudoWithoutPasswordRequired) {
                    require(
                        password != null,
                        { "User ${prov.whoami()} not able to sudo on remote machine without password and no password available for the user." })
                    prov.makeUserSudoerWithNoSudoPasswordRequired(password)
                } else {
                    throw IllegalStateException("User ${prov.whoami()} not able to sudo on remote machine without password and option not set to enable user to sudo without password.")
                }
            }
            return prov
        } else {
            throw IllegalArgumentException("Error: neither a valid localHost nor a valid remoteHost was specified! Use option -h for help.")
        }
    } else {
        println("Invalid command line options.\nPlease use option -h for help.")
        exitProcess(1)
    }
}


private fun retrievePassword(cliCommand: CliCommand): Secret? {
    var password: Secret? = null
    if (cliCommand.isValidRemote()) {
        if (cliCommand.sshWithPasswordPrompt) {
            password =
                PromptSecretSource("Password for user $cliCommand.userName!! on $cliCommand.remoteHost!!").secret()
        } else if (cliCommand.sshWithGopassPath != null) {
            password = GopassSecretSource(cliCommand.sshWithGopassPath).secret()
        }
    }
    return password
}
