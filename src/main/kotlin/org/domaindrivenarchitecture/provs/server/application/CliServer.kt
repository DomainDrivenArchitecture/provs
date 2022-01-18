package org.domaindrivenarchitecture.provs.server.application

import org.domaindrivenarchitecture.provs.framework.core.cli.createProvInstance
import org.domaindrivenarchitecture.provs.server.domain.installK3sAsContainers
import kotlin.system.exitProcess


/**
 * Provisions a server, either locally or on a remote machine depending on the given arguments.
 * Depending on the cli parameter "type" it will install the k3s server as standalone or as a container.
 *
 * Get help with option -h
 */
fun main(args: Array<String>) {

    val cmd = parseServerArguments("java -jar provs-server.jar", args)
    if (!cmd.isValid()) {
        println("Arguments are not valid, pls try -h for help.")
        exitProcess(1)
    }
    val prov = createProvInstance(cmd.target)

    when (cmd.type()) {
        CliServerArgumentsParser.K3sType.K3S -> prov.provisionK3s()
        CliServerArgumentsParser.K3sType.K3D -> prov.installK3sAsContainers()
    }
}
