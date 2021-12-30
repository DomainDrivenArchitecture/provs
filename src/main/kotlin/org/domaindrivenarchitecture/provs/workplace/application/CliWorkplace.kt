package org.domaindrivenarchitecture.provs.workplace.application

import org.domaindrivenarchitecture.provs.core.cli.createProvInstance
import org.domaindrivenarchitecture.provs.workplace.infrastructure.getConfig
import kotlin.system.exitProcess


/**
 * Provisions a workplace locally or on a remote machine. Use option -h for help.
 */
fun main(args: Array<String>) {

    val cmd = CliWorkplaceParser("java -jar provs.jar").parseWorkplaceArguments(args)

    if (!cmd.isValid()) {
        println("Arguments are not valid, pls try option -h for help.")
        exitProcess(1)
    }

    try {
        // retrieve config
        val conf = getConfig(cmd.configFile)

        // create
        val prov = createProvInstance(cmd.target, remoteHostSetSudoWithoutPasswordRequired = true)
        provision(prov, conf)

    } catch (e: IllegalArgumentException) {
        println(
            "Error: File\u001b[31m ${cmd.configFile} \u001b[0m was not found.\n" +
                    "Pls copy file \u001B[31m WorkplaceConfigExample.yaml \u001B[0m to file \u001B[31m ${cmd.configFile} \u001B[0m " +
                    "and change the content according to your needs.\n"
        )
    }
}
