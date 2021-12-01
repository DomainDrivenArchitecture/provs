package org.domaindrivenarchitecture.provs.extensions.server_software.k3s.application

import org.domaindrivenarchitecture.provs.core.cli.createProvInstance
import org.domaindrivenarchitecture.provs.core.cli.parseCli


/**
 * Provisions a k3s server, either locally or on a remote machine depending on the given arguments.
 *
 * Get help with option -h
 */
fun main(args: Array<String>) {
    val cmd = parseCli("java -jar provs-server.jar", args)
    val prov = createProvInstance(cmd)

    prov.provisionK3s()
}
