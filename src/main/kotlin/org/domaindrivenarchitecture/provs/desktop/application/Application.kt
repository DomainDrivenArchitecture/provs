package org.domaindrivenarchitecture.provs.desktop.application

import kotlinx.serialization.SerializationException
import org.domaindrivenarchitecture.provs.desktop.domain.provisionDesktopCmd
import org.domaindrivenarchitecture.provs.framework.core.cli.createProvInstance
import java.io.FileNotFoundException
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

    val prov = createProvInstance(cmd.target, remoteHostSetSudoWithoutPasswordRequired = true)

    try {
        provisionDesktopCmd(prov, cmd)
    } catch (e: SerializationException) {
        println(
            "Error: File \"${cmd.configFile?.fileName}\" has an invalid format and or invalid data.\n"
        )
    } catch (e: FileNotFoundException) {
        println(
            "Error: File\u001b[31m ${cmd.configFile?.fileName} \u001b[0m was not found.\n" +
                    "Pls copy file \u001B[31m desktop-config-example.yaml \u001B[0m to file \u001B[31m ${cmd.configFile?.fileName} \u001B[0m " +
                    "and change the content according to your needs.\n"
        )
    }
}
