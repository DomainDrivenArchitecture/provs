package org.domaindrivenarchitecture.provs.core.platforms

import org.domaindrivenarchitecture.provs.core.ProgressType
import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.ProvResult
import org.domaindrivenarchitecture.provs.core.processors.LocalProcessor
import org.domaindrivenarchitecture.provs.core.processors.Processor


class WinProv internal constructor(processor : Processor = LocalProcessor(), name: String? = null, progressType: ProgressType)
    : Prov(processor, name, progressType) {

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