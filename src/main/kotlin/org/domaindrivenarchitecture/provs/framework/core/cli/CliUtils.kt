package org.domaindrivenarchitecture.provs.framework.core.cli

import org.domaindrivenarchitecture.provs.configuration.domain.TargetCliCommand
import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.core.local
import org.domaindrivenarchitecture.provs.framework.core.remote
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.secretSources.PromptSecretSource
import kotlin.system.exitProcess


/**
 * Returns a Prov instance according to the targetCommand.
 * Returns a local Prov instance if targetCommand.isValidLocalhost() is true resp.
 * returns a remote Prov instance if targetCommand.isValidRemote() is true.
 */
fun createProvInstance(targetCommand: TargetCliCommand): Prov {
    if (targetCommand.isValid()) {
        val password: Secret? = targetCommand.remoteTarget()?.password

        return if (targetCommand.isValidLocalhost()) {
            local()
        } else if (targetCommand.isValidRemote()) {
            createRemoteProvInstance(targetCommand.remoteTarget(), password)
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


/**
 * Wrapper for exitProcess, which allows e.g. mocking for test purposes
 */
fun quit(status: Int): Nothing {
    exitProcess(status)
}


fun printProvsVersion() {
    // see https://stackoverflow.com/questions/33020069/how-to-get-version-attribute-from-a-gradle-build-to-be-included-in-runtime-swing
    val version = object {}.javaClass.getPackage().getImplementationVersion()
    if (version != null) {
        println("\nProvs version: $version\n")
    }
}


internal fun createRemoteProvInstance(
    target: TargetCliCommand.RemoteTarget?,
    password: Secret? = null
): Prov {
    return if (target != null) {
        remote(target.host, target.user, target.password ?: password)
    } else {
        throw IllegalArgumentException(
            "Error: no valid remote target (host & user) was specified!"
        )
    }
}


internal fun getPasswordToConfigureSudoWithoutPassword(): Secret {
    return PromptSecretSource("password to configure sudo without password.").secret()
}
