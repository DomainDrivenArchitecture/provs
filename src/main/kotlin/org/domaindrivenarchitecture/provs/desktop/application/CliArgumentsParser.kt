package org.domaindrivenarchitecture.provs.desktop.application

import kotlinx.cli.ArgType
import kotlinx.cli.Subcommand
import org.domaindrivenarchitecture.provs.configuration.application.CliTargetParser
import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.configuration.domain.TargetCliCommand
import org.domaindrivenarchitecture.provs.desktop.domain.DesktopCliCommand
import org.domaindrivenarchitecture.provs.desktop.domain.DesktopType


open class CliArgumentsParser(name: String) : CliTargetParser(name) {

    private val modules: List<DesktopSubcommand> = listOf(Basic(), Office(), Ide())

    init {
        subcommands(*modules.toTypedArray())
    }

    fun parseCommand(args: Array<String>): DesktopCliCommand {
        super.parse(args)

        val module = modules.first { it.parsed }

        return DesktopCliCommand(
            DesktopType.valueOf(module.name.uppercase()),
            TargetCliCommand(
                localHost,
                remoteHost,
                userName,
                sshWithPasswordPrompt,
                sshWithGopassPath,
                sshWithKey
            ),
            module.configFileName
        )
    }

    abstract class DesktopSubcommand(name: String, description: String) : Subcommand(name, description) {
        var parsed: Boolean = false
        var configFileName: ConfigFileName? = null
        val cliConfigFileName by option(
            ArgType.String,
            "config-file",
            "c",
            "the filename containing the yaml config",
        )

        override fun execute() {
            configFileName = cliConfigFileName?.let { ConfigFileName(it) }
            parsed = true
        }
    }

    class Basic : DesktopSubcommand("basic", "basic desktop for a user")
    class Office : DesktopSubcommand("office", "includes office software like Thunderbird, LibreOffice, etc")
    class Ide : DesktopSubcommand("ide", "includes office software as well as ides like VSCode, etc")
}