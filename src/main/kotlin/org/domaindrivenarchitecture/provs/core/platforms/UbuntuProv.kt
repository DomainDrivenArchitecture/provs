package org.domaindrivenarchitecture.provs.core.platforms

import org.domaindrivenarchitecture.provs.core.ProgressType
import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.ProvResult
import org.domaindrivenarchitecture.provs.core.escapeAndEncloseByDoubleQuoteForShell
import org.domaindrivenarchitecture.provs.core.processors.LocalProcessor
import org.domaindrivenarchitecture.provs.core.processors.Processor

const val SHELL = "/bin/bash"


class UbuntuProv internal constructor(processor : Processor = LocalProcessor(), name: String? = null, progressType: ProgressType)
    : Prov(processor, name, progressType) {

    override fun cmd(cmd: String, dir: String?, sudo: Boolean) : ProvResult = def {
        xec(SHELL, "-c", commandWithDirAndSudo(cmd, dir, sudo))
    }

    override fun cmdNoLog(cmd: String, dir: String?, sudo: Boolean) : ProvResult {
        return xecNoLog(SHELL, "-c", commandWithDirAndSudo(cmd, dir, sudo))
    }

    override fun cmdNoEval(cmd: String, dir: String?, sudo: Boolean) : ProvResult {
        return xec(SHELL, "-c", commandWithDirAndSudo(cmd, dir, sudo))
    }
}

private fun commandWithDirAndSudo(cmd: String, dir: String?, sudo: Boolean): String {
    val cmdWithDir= if (dir == null) cmd else "cd $dir && $cmd"
    return if (sudo) cmdWithDir.sudoize() else cmdWithDir
}

private fun String.sudoize(): String {
    return "sudo " + SHELL + " -c " + this.escapeAndEncloseByDoubleQuoteForShell()
}