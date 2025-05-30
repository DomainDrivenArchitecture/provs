package org.domaindrivenarchitecture.provs.server.application

import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import org.domaindrivenarchitecture.provs.configuration.application.CliTargetParser
import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.configuration.domain.TargetCliCommand
import org.domaindrivenarchitecture.provs.server.domain.ServerCliCommand
import org.domaindrivenarchitecture.provs.server.domain.ServerType
import org.domaindrivenarchitecture.provs.server.domain.k3s.ApplicationFileName
import org.domaindrivenarchitecture.provs.server.domain.k3s.K3sCliCommand
import org.domaindrivenarchitecture.provs.server.domain.k3s.ServerOnlyModule

@OptIn(ExperimentalCli::class)
class CliArgumentsParser(name: String) : CliTargetParser(name) {

    private val modules: List<ServerSubcommand> = listOf(K3s(), K3d())

    init {
        subcommands(*modules.toTypedArray())
    }

    fun parseCommand(args: Array<String>): ServerCliCommand {
        super.parse(args)

        val module = modules.first { it.parsed }

        val serverType = ServerType.valueOf(module.name.uppercase())
        when(serverType) {
            ServerType.K3S -> return K3sCliCommand(
                ServerType.valueOf(module.name.uppercase()),
                TargetCliCommand(
                    target,
                    passwordInteractive
                ),
                module.configFileName,
                module.applicationFileNames,
                module.onlyModules,
                module.reprovision,
            )
            else -> return ServerCliCommand(
                ServerType.valueOf(module.name.uppercase()),
                TargetCliCommand(
                    target,
                    passwordInteractive
                ),
                module.configFileName
            )
        }
    }

    abstract class ServerSubcommand(name: String, description: String) : Subcommand(name, description) {
        var parsed: Boolean = false
        var configFileName: ConfigFileName? = null
        var applicationFileNames: List<ApplicationFileName>? = null
        var onlyModules: List<String>? = null
        var reprovision: Boolean = false
    }

    class K3s : ServerSubcommand("k3s", "the k3s module, 'provs-server.jar k3s --help' for more info") {
        val cliConfigFileName by option(
            ArgType.String,
            "config-file",
            "c",
            "the filename containing the yaml config for k3s"
        )
        val cliApplicationFileName by option(
            ArgType.String,
            "application-file",
            "a",
            "one or more filenames (comma-separated) containing application deployment yaml"
        )
        val only by option(
            ArgType.Choice<ServerOnlyModule>(),
            "only",
            "o",
            "provisions only parts ",
        )
        val cliReprovision by option(
            ArgType.Boolean,
            "reprovision",
            "r",
            "redo provisioning, deletes old config first",
        )
        override fun execute() {
            super.configFileName = cliConfigFileName?.let { ConfigFileName(it) }
            super.applicationFileNames = cliApplicationFileName?.split(",")?.map { ApplicationFileName(it) }
            super.onlyModules = only?.let { listOf(it.name.lowercase()) }
            super.reprovision = cliReprovision == true
            super.parsed = true
        }
    }

    class K3d : ServerSubcommand("k3d", "the k3d module, 'provs-server.jar k3d --help' for more info") {
        override fun execute() {
            TODO("Not yet implemented")
        }
    }


}

