package org.domaindrivenarchitecture.provs.extensions.server_software.k3s.application

import org.domaindrivenarchitecture.provs.framework.core.cli.TargetCliCommand


class ServerCliCommand(private val k3sType: String, val target: TargetCliCommand) {
    fun isValid(): Boolean {
        return target.isValid() && hasValidK3sType()
    }
    private fun hasValidK3sType(): Boolean {
        return CliK3sArgumentsParser.K3sType.values().map { it.name }.contains(k3sType.uppercase())
    }
    fun type() = CliK3sArgumentsParser.K3sType.valueOf(k3sType.uppercase())
}

fun parseServerArguments(
    programName: String = "java -jar provs.jar",
    args: Array<String>
): ServerCliCommand {
    val parser = CliK3sArgumentsParser(programName)
    parser.parse(args)

    return ServerCliCommand(
        parser.type,
        TargetCliCommand(
        parser.localHost,
        parser.remoteHost,
        parser.userName,
        parser.sshWithPasswordPrompt,
        parser.sshWithGopassPath,
        parser.sshWithKey
    )
    )
}


