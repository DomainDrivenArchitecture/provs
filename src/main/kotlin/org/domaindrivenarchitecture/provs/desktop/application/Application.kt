package org.domaindrivenarchitecture.provs.desktop.application

import kotlinx.serialization.SerializationException
import org.domaindrivenarchitecture.provs.configuration.application.ensureSudoWithoutPassword
import org.domaindrivenarchitecture.provs.desktop.domain.DesktopConfig
import org.domaindrivenarchitecture.provs.desktop.domain.provisionDesktopCommand
import org.domaindrivenarchitecture.provs.desktop.infrastructure.getConfig
import org.domaindrivenarchitecture.provs.framework.core.cli.createProvInstance
import org.domaindrivenarchitecture.provs.framework.core.cli.quit
import java.io.FileNotFoundException
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.system.exitProcess

/**
 * Provisions desktop software (office and/or ide depending on type) locally or on a remote machine. Use option -h for help.
 */
fun main(args: Array<String>) {

    val cmd = CliArgumentsParser("provs-desktop.jar subcommand target").parseCommand(args)
    if (!cmd.isValid()) {
        println("Arguments are not valid, pls try option -h for help.")
        exitProcess(1)
    }

    val defaultConfigFileName = "desktop-config.yaml"
    val config = if ((cmd.configFile == null) && !Files.isRegularFile(Path(defaultConfigFileName))) {
        println("ATTENTION: No config provided => using an empty config.")
        DesktopConfig()
    } else {
        val configFileName = cmd.configFile?.fileName ?: defaultConfigFileName
        try {
            getConfig(configFileName)
        } catch (e: SerializationException) {
            println(
                "Error: File \"${configFileName}\" has an invalid format and or invalid data."
            )
            null
        } catch (e: FileNotFoundException) {
            println(
                "Error: File\u001b[31m ${configFileName} \u001b[0m was not found.\n" +
                        "Pls copy file \u001B[31m desktop-config-example.yaml \u001B[0m to file \u001B[31m ${configFileName} \u001B[0m " +
                        "and change the content according to your needs."
            )
            null
        }
    }

    if (config == null) {
        println("No suitable config found.")
        quit(-1)
    }

    val prov = createProvInstance(cmd.target)

    prov.session {
        ensureSudoWithoutPassword(cmd.target.remoteTarget()?.password)
        provisionDesktopCommand(cmd, config)
    }
}

