package io.provs.platforms

import io.provs.Prov
import io.provs.ProvResult
import io.provs.processors.LocalProcessor
import io.provs.processors.Processor


class WinProv internal constructor(processor : Processor = LocalProcessor(), name: String? = null) : Prov (processor, name) {

    private val SHELL = "cmd.exe"

    override fun cmd(cmd: String, dir: String?, sudo: Boolean) : ProvResult = def {
        require(!sudo, {"sudo not supported"})
        xec(SHELL, "/c", if (dir == null) cmd else "cd $dir && $cmd")
    }

    override fun cmdNoLog(cmd: String, dir: String?, sudo: Boolean) : ProvResult = def {
        require(!sudo, {"sudo not supported"})
        xecNoLog(SHELL, "/c", if (dir == null) cmd else "cd $dir && $cmd")
    }


    override fun cmdNoEval(cmd: String, dir: String?, sudo: Boolean) : ProvResult {
        require(!sudo, {"sudo not supported"})
        return xec(SHELL, "/c", if (dir == null) cmd else "cd $dir && $cmd")
    }
}