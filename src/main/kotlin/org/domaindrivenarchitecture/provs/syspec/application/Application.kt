package org.domaindrivenarchitecture.provs.syspec.application

import org.domaindrivenarchitecture.provs.framework.core.cli.createProvInstance
import org.domaindrivenarchitecture.provs.syspec.domain.verifySpec


/**
 * Runs a check according to the specification file (default file: syspec-config.yaml).
 * The check is performed either locally or on a remote system depending on the given arguments.
 *
 * Get help with option -h
 */
fun main(args: Array<String>) {

    val checkedArgs = if (args.isEmpty()) arrayOf("-h") else args

    val cmd = CliArgumentsParser("provs-syspec.jar target").parseCommand(checkedArgs)

    createProvInstance(cmd.target).verifySpec(cmd.configFileName)
}
