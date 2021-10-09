package org.domaindrivenarchitecture.provs.workplace.application

import org.domaindrivenarchitecture.provs.core.*
import org.domaindrivenarchitecture.provs.workplace.infrastructure.getConfig
import org.domaindrivenarchitecture.provs.ubuntu.secret.secretSources.GopassSecretSource
import org.domaindrivenarchitecture.provs.ubuntu.secret.secretSources.PromptSecretSource

import java.lang.RuntimeException

/**
 * Provisions according to the options either a meissa workplace, reposOnly or gopassOnly.
 * Locally or on a remote machine. If remotely, the remote host and remote user are specified by args parameters.
 *
 * Get help with:
 * java -jar build/libs/provs-meissa-latest.jar meissa.provs.application.CliKt main -h
 */
fun main(args: Array<String>) {
    val cliCommand = parseCli(args)
    if (cliCommand.isValid()) {
        provision(cliCommand)
    } else {
        println("Invalid command line options.\nPlease use option -h for help.")
        System.exit(1)
    }
}

private fun provision(cliCommand: CliCommand) {
    val filename = cliCommand.configFileName

    // TODO: improve exceptions
    try {
        val conf = getConfig(filename)
        val password: Secret? = retrievePassword(cliCommand)
        val prov: Prov = createProvInstance(cliCommand, password)

        prov.provision(conf)
    } catch (e: IllegalArgumentException) {
        println(
            "Error: File\u001b[31m $filename \u001b[0m was not found.\n" +
                    "Pls copy file \u001B[31m MeissaWorkplaceConfigExample.yaml \u001B[0m to file \u001B[31m $filename \u001B[0m " +
                    "and change the content according to your needs.\n"
        )
    }
}

private fun createProvInstance(
    cliCommand: CliCommand,
    password: Secret?
): Prov {
    if (cliCommand.isValid()) {
        if (cliCommand.isValidRemote()) {
            val host = cliCommand.remoteHost!!
            val remoteUser = cliCommand.userName!!
            if (cliCommand.sshWithKey) {
                return remote(host, remoteUser)
            } else {
                require(
                    password != null,
                    { "No password available for provisioning without ssh keys. Either specify provisioning by ssh-keys or provide password." })
                return remote(host, remoteUser, password)
            }
        } else {
            return local()
        }
    } else {
        throw RuntimeException("Invalid cliCommand")
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