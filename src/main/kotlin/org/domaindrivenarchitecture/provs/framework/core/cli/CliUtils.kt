package org.domaindrivenarchitecture.provs.framework.core.cli

import org.domaindrivenarchitecture.provs.configuration.domain.TargetCliCommand
import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.core.local
import org.domaindrivenarchitecture.provs.framework.core.remote
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.secretSources.PromptSecretSource
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.base.currentUserCanSudoWithoutPassword
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.base.makeCurrentUserSudoerWithoutPasswordRequired
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.base.whoami
import kotlin.system.exitProcess


/**
 * Returns a Prov instance according to the targetCommand.
 * Returns a local Prov instance if targetCommand.isValidLocalhost() is true resp.
 * returns a remote Prov instance if targetCommand.isValidRemote() is true.
 */
fun createProvInstance(targetCommand: TargetCliCommand): Prov {
    if (targetCommand.isValid()) {
        val password: Secret? = targetCommand.remoteTarget()?.password

        val remoteTarget = targetCommand.remoteTarget()

        return if (targetCommand.isValidLocalhost()) {
            createLocalProvInstance()
        } else if (targetCommand.isValidRemote() && remoteTarget != null) {
            createRemoteProvInstance(
                remoteTarget.host,
                remoteTarget.user,
                remoteTarget.password == null,
                password
            )
        } else {
            throw IllegalArgumentException(
                "Error: neither a valid localHost nor a valid remoteHost was specified! Use option -h for help."
            )
        }
    } else {
        println("ERROR: Invalid target (${targetCommand.target}). Please use option -h for help.")
        System.out.flush()
        exitProcess(1)
    }
}

private fun createLocalProvInstance(): Prov {
    val prov = local()
    if (!prov.currentUserCanSudoWithoutPassword()) {
        val passwordNonNull = getPasswordToConfigureSudoWithoutPassword()

        prov.makeCurrentUserSudoerWithoutPasswordRequired(passwordNonNull)

        check(prov.currentUserCanSudoWithoutPassword()) {
                    "ERROR: User ${prov.whoami()} cannot sudo without enteringa password."
        }
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
            require(password != null) {
                "No password available for provisioning without ssh keys. " +
                        "Either specify provisioning by ssh-keys or provide password."
            }
            remote(host, remoteUser, password)
        }

    return if (prov.currentUserCanSudoWithoutPassword()) {
        prov
    } else {

        val passwordNonNull = password
            ?: getPasswordToConfigureSudoWithoutPassword()

        val result = prov.makeCurrentUserSudoerWithoutPasswordRequired(passwordNonNull)

        check(result.success) {
            "Could not make user a sudoer without password required. (Maybe the provided password is incorrect.)"
        }

        // a new session is required after the user has become a sudoer without password
        val provWithNewSshClient = remote(host, remoteUser, password)

        check(provWithNewSshClient.currentUserCanSudoWithoutPassword()) {
            "ERROR: User ${provWithNewSshClient.whoami()} on $host cannot sudo without entering a password."
        }

        provWithNewSshClient
    }
}

internal fun getPasswordToConfigureSudoWithoutPassword(): Secret {
    return PromptSecretSource("password to configure sudo without password.").secret()
}