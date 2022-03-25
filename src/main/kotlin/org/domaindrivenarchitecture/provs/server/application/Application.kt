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

    val cmd = CliArgumentsParser("provs-server.jar").parseCommand(checkedArgs)
    if (!cmd.isValid()) {
        println("Arguments are not valid, pls try -h for help.")
        exitProcess(1)
    }
    val prov = createProvInstance(cmd.target)
    when(cmd.serverType) {
        ServerType.K3S -> prov.provisionK3s(cmd as K3sCliCommand)
        else -> { throw RuntimeException("Unknown serverType") }
    }
}
