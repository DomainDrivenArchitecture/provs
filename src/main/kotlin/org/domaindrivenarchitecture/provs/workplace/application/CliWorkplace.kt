package org.domaindrivenarchitecture.provs.workplace.application

import org.domaindrivenarchitecture.provs.core.cli.createProvInstance
import org.domaindrivenarchitecture.provs.workplace.application.WorkplaceCliCommand.Companion.parseWorkplaceArguments
import org.domaindrivenarchitecture.provs.workplace.infrastructure.getConfig
import java.io.File
import kotlin.system.exitProcess


/**
 * Provisions according to the options either a meissa workplace, reposOnly or gopassOnly.
 * Locally or on a remote machine. If remotely, the remote host and remote user are specified by args parameters.
 */
fun main(args: Array<String>) {

    val cmd = parseWorkplaceArguments("java -jar provs.jar", args)
    if (!cmd.isValid()) {
        println("Arguments are not valid, pls try -h for help.")
        exitProcess(1)
    }

    provisionWorkplace(cmd)
}


private fun provisionWorkplace(cliCommand: WorkplaceCliCommand) {
    val filename = cliCommand.configFile

    try {
        val conf = getConfig(filename)

        val prov = createProvInstance(cliCommand.target)
        prov.provision(conf)

    } catch (e: IllegalArgumentException) {
        println(
            "Error: File\u001b[31m $filename \u001b[0m was not found.\n" +
                    "Pls copy file \u001B[31m WorkplaceConfigExample.yaml \u001B[0m to file \u001B[31m $filename \u001B[0m " +
                    "and change the content according to your needs.\n"
        )

        // provide example config
        File("WorkplaceConfigExample.yaml").writeText("type: \"MINIMAL\"\n")
    }
}
