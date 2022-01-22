package org.domaindrivenarchitecture.provs.server.application

import kotlinx.cli.Subcommand
import org.domaindrivenarchitecture.provs.framework.core.cli.CliTargetParser
import org.domaindrivenarchitecture.provs.framework.core.cli.TargetCliCommand
import org.domaindrivenarchitecture.provs.server.domain.ServerCliCommand
import org.domaindrivenarchitecture.provs.server.domain.ServerType

class CliArgumentsParser(
    name: String
    ) : CliTargetParser(name) {

    private val modules: List<ServerSubcommand> = listOf(K3s(), K3d())
    init {
        subcommands(*modules.toTypedArray())
    }

    fun parseCommand(args: Array<String>): ServerCliCommand {
        super.parse(args)

        val module = modules.first { it.parsed }

        return ServerCliCommand(
            ServerType.valueOf(module.name.uppercase()),
            TargetCliCommand(
                localHost,
                remoteHost,
                userName,
                sshWithPasswordPrompt,
                sshWithGopassPath,
                sshWithKey
            )
        )
    }

    abstract class ServerSubcommand(name: String, description: String): Subcommand(name, description) {
        var parsed = false
    }

    class K3s: ServerSubcommand("k3s", "the k3s module") {
        override fun execute() {
            parsed = true
        }
    }

    class K3d: ServerSubcommand("k3d", "the k3s module") {
        override fun execute() {
            TODO("Not yet implemented")
        }
    }



}

