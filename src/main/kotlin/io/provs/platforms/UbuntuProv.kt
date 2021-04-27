package io.provs.platforms

import io.provs.Prov
import io.provs.ProvResult
import io.provs.escapeAndEncloseByDoubleQuoteForShell
import io.provs.processors.LocalProcessor
import io.provs.processors.Processor

const val SHELL = "/bin/bash"  // could be changed to another shell like "sh", "/bin/csh" if required


class UbuntuProv internal constructor(processor : Processor = LocalProcessor(), name: String? = null) : Prov (processor, name) {

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