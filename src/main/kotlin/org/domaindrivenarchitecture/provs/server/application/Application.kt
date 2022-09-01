package org.domaindrivenarchitecture.provs.server.application

import org.domaindrivenarchitecture.provs.framework.core.cli.createProvInstance
import org.domaindrivenarchitecture.provs.server.domain.ServerType
import org.domaindrivenarchitecture.provs.server.domain.k3s.K3sCliCommand
import org.domaindrivenarchitecture.provs.server.domain.k3s.provisionK3s
import kotlin.system.exitProcess


/**
 * Provisions a server, either locally or on a remote machine depending on the given arguments.
 * Depending on the cli parameter "type" it will install the k3s server as standalone or as a container.
 *
 * Get help with option -h
 */
fun main(args: Array<String>) {

    val checkedArgs = if (args.isEmpty()) arrayOf("-h") else args

    // validate subcommand
    if (!checkedArgs.contains("-h") && !ServerType.values().any {it.name.lowercase() == checkedArgs[0]}) {
        println("Unknown serverType. Currently only k3s is accepted.")
        exitProcess(1)
    }

    val cmd = CliArgumentsParser("provs-server.jar subcommand target").parseCommand(checkedArgs)

    // ToDo: exitProcess makes testing harder, find another solution
    // validate parsed arguments
    if (!cmd.isValidTarget()) {
        println("Remote or localhost not valid, please try -h for help.")
        exitProcess(1)
    }

    val prov = createProvInstance(cmd.target)
    prov.provisionK3s(cmd as K3sCliCommand)

}
