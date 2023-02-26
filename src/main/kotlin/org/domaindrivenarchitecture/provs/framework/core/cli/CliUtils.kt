package org.domaindrivenarchitecture.provs.framework.core.cli

import org.domaindrivenarchitecture.provs.configuration.domain.TargetCliCommand
import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.core.local
import org.domaindrivenarchitecture.provs.framework.core.remote
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.secretSources.PromptSecretSource
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.base.currentUserCanSudoWithoutPassword
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.base.makeUserSudoerWithNoSudoPasswordRequired
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.base.whoami
import kotlin.system.exitProcess


/**
 * Returns a Prov instance according to the targetCommand.
 * E.g. it returns a local Prov instance if targetCommand.isValidLocalhost() is true or
 * returns a remote Prov instance if targetCommand.isValidRemote() is true.
 *
 * If the target is remote and if parameter remoteHostSetSudoWithoutPasswordRequired is set to true,
 * it will enable sudo without password on the remote machine (in case this was not yet enabled).
 */
fun createProvInstance(targetCommand: TargetCliCommand): Prov {
    if (targetCommand.isValid()) {
        val password: Secret? = targetCommand.remoteTarget()?.password

        val remoteTarget = targetCommand.remoteTarget()
        if (targetCommand.isValidLocalhost()) {
            return createLocalProvInstance()
        } else if (targetCommand.isValidRemote() && remoteTarget != null) {
            return createRemoteProvInstance(
                remoteTarget.host,
                remoteTarget.user,
                remoteTarget.password == null,
                password
            )
        } else {
            throw IllegalArgumentException("Error: neither a valid localHost nor a valid remoteHost was specified! Use option -h for help.")
        }
    } else {
        println("Invalid command line options.\nPlease use option -h for help.")
        exitProcess(1)
    }
}

private fun createLocalProvInstance(): Prov {
    val prov = local()
    if (!prov.currentUserCanSudoWithoutPassword()) {
        val password = PromptSecretSource(
            "Please enter password to configure sudo without password in the future." +
                    "\nWarning: This will permanently allow your user to use sudo privileges without a password."
        ).secret()
        prov.makeUserSudoerWithNoSudoPasswordRequired(password)
    }
    return prov
}


private fun createRemoteProvInstance(
    host: String,
    remoteUser: String,
    sshWithKey: Boolean,
    password: Secret?
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

    if (!prov.currentUserCanSudoWithoutPassword()) {
        require(
            password != null,
            { "User ${prov.whoami()} not able to sudo on remote machine without password and no password available for the user." })
        prov.makeUserSudoerWithNoSudoPasswordRequired(password)

        // a new session is required after making the user a sudoer without password
        return remote(host, remoteUser, password)
    }
    return prov
}


internal fun retrievePassword(cliCommand: TargetCliCommand): Secret? {
    var password: Secret? = null
    if (cliCommand.isValidRemote() && cliCommand.passwordInteractive) {
        password =
            PromptSecretSource("Password for user $cliCommand.userName!! on $cliCommand.remoteHost!!").secret()

    }
    return password
}
