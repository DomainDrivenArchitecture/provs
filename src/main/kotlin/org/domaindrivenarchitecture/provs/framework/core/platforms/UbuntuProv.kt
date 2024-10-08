package org.domaindrivenarchitecture.provs.framework.core.platforms

import org.domaindrivenarchitecture.provs.framework.core.*
import org.domaindrivenarchitecture.provs.framework.core.processors.LocalProcessor
import org.domaindrivenarchitecture.provs.framework.core.processors.Processor

const val SHELL = "/bin/bash"


open class UbuntuProv(
    processor: Processor = LocalProcessor(),
    name: String? = null,
    progressType: ProgressType = ProgressType.BASIC
) : Prov(processor, name, progressType) {

    override fun cmd(cmd: String, dir: String?, sudo: Boolean): ProvResult = taskWithResult {
        exec(SHELL, "-c", commandWithDirAndSudo(cmd, dir, sudo))
    }

    override fun cmdNoLog(cmd: String, dir: String?, sudo: Boolean): ProvResult {
        return execNoLog(SHELL, "-c", commandWithDirAndSudo(cmd, dir, sudo))
    }

    override fun cmdNoEval(cmd: String, dir: String?, sudo: Boolean): ProvResult {
        return exec(SHELL, "-c", commandWithDirAndSudo(cmd, dir, sudo))
    }

    override fun execInContainer(containerName: String, vararg args: String): Array<String> {
        return arrayOf(SHELL, "-c", "sudo docker exec $containerName " + buildCommand(*args))
    }

    private fun buildCommand(vararg args: String): String {
        return if (args.size == 1) {
            args[0].escapeAndEncloseByDoubleQuoteForShell()
        } else {
            if (args.size == 3 && SHELL == args[0] && "-c" == args[1]) {
                SHELL + " -c " + args[2].escapeAndEncloseByDoubleQuoteForShell()
            } else {
                args.joinToString(separator = " ")
            }
        }
    }
}

private fun commandWithDirAndSudo(cmd: String, dir: String?, sudo: Boolean): String {
    val cmdWithDir = if (dir == null) cmd else "cd $dir && $cmd"
    return if (sudo) cmdWithDir.sudoizeCommand() else cmdWithDir
}

/**
 * Returns a command encapsulated in a shell command and executed with sudo.
 * For simple cases consider using sudo as prefix if the command instead.
 */
internal fun String.sudoizeCommand(): String {
    return "sudo -E " + SHELL + " -c " + this.escapeAndEncloseByDoubleQuoteForShell()
}