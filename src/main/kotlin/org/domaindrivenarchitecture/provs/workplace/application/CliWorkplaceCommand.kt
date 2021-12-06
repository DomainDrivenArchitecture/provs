package org.domaindrivenarchitecture.provs.workplace.application

import org.domaindrivenarchitecture.provs.core.cli.TargetCliCommand


class WorkplaceCliCommand(val configFile: String, val target: TargetCliCommand) {
    companion object {
        fun parseWorkplaceArguments(
            programName: String = "java -jar provs.jar",
            args: Array<String>
        ): WorkplaceCliCommand {
            val parser = CliWorkplaceParser(programName)
            parser.parse(args)

            return WorkplaceCliCommand(
                parser.configFileName ?: "WorkplaceConfig.yaml",
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
    }

    fun isValid(): Boolean {
        return target.isValid()
    }
}

