package org.domaindrivenarchitecture.provs.core.cli

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


/**
 * Returns a Prov instance according to the targetCommand.
 * E.g. it returns a local Prov instance if targetCommand.isValidLocalhost() is true or
 * returns a remote Prov instance if targetCommand.isValidRemote() is true.
 *
 * If the target is remote and if parameter remoteHostSetSudoWithoutPasswordRequired is set to true,
 * it will enable sudo without password on the remote machine (in case this was not yet enabled).
 */
internal fun createProvInstance(
    targetCommand: TargetCliCommand,
    remoteHostSetSudoWithoutPasswordRequired: Boolean = false
): Prov {
    if (targetCommand.isValid()) {
        val password: Secret? = if (targetCommand.isValidRemote()) retrievePassword(targetCommand) else null

        if (targetCommand.isValidLocalhost()) {
            return local()
        } else if (targetCommand.isValidRemote()) {
            return createProvInstanceRemote(
                targetCommand.remoteHost!!,
                targetCommand.userName!!,
                targetCommand.sshWithKey,
                password,
                remoteHostSetSudoWithoutPasswordRequired
            )
        } else {
            throw IllegalArgumentException("Error: neither a valid localHost nor a valid remoteHost was specified! Use option -h for help.")
        }
    } else {
        println("Invalid command line options.\nPlease use option -h for help.")
        exitProcess(1)
    }
}

private fun createProvInstanceRemote(
    host: String,
    remoteUser: String,
    sshWithKey: Boolean,
    password: Secret?,
    remoteHostSetSudoWithoutPasswordRequired: Boolean
): Prov {
    val prov =
        if (sshWithKey) {
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

            // a new session is required after making the user a sudoer without password
            return remote(host, remoteUser, password)
        } else {
            throw IllegalStateException("User ${prov.whoami()} not able to sudo on remote machine without password and option not set to enable user to sudo without password.")
        }
    }
    return prov
}


internal fun retrievePassword(cliCommand: TargetCliCommand): Secret? {
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
