package org.domaindrivenarchitecture.provs.server.application

import kotlinx.cli.ArgType
import kotlinx.cli.Subcommand
import org.domaindrivenarchitecture.provs.configuration.application.CliTargetParser
import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.configuration.domain.TargetCliCommand
import org.domaindrivenarchitecture.provs.server.domain.ServerCliCommand
import org.domaindrivenarchitecture.provs.server.domain.ServerType

class CliArgumentsParser(name: String) : CliTargetParser(name) {

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
                target,
                passwordInteractive
            ),
            module.configFileName
        )
    }

    abstract class ServerSubcommand(name: String, description: String) : Subcommand(name, description) {
        var parsed: Boolean = false
        var configFileName: ConfigFileName? = null
    }

    class K3s : ServerSubcommand("k3s", "the k3s module") {
        val cliConfigFileName by option(
            ArgType.String,
            "config-file",
            "c",
            "the filename containing the yaml config for k3s"
        )

        override fun execute() {
            super.configFileName = cliConfigFileName?.let { ConfigFileName(it) }
            super.parsed = true
        }
    }

    class K3d : ServerSubcommand("k3d", "the k3s module") {
        override fun execute() {
            TODO("Not yet implemented")
        }
    }


}

