package io.provs.platforms

import io.provs.Prov
import io.provs.ProvResult
import io.provs.processors.LocalProcessor
import io.provs.processors.Processor

const val SHELL = "/bin/bash"  // could be changed to another shell like "sh", "/bin/csh" if required


class UbuntuProv internal constructor(processor : Processor = LocalProcessor(), name: String? = null) : Prov (processor, name) {

    override fun cmd(cmd: String, dir: String?) : ProvResult = def {
        xec(SHELL, "-c", if (dir == null) cmd else "cd $dir && $cmd")
    }

    override fun cmdNoLog(cmd: String, dir: String?) : ProvResult {
        return xecNoLog(SHELL, "-c", if (dir == null) cmd else "cd $dir && $cmd")
    }

    override fun cmdNoEval(cmd: String, dir: String?) : ProvResult {
        return xec(SHELL, "-c", if (dir == null) cmd else "cd $dir && $cmd")
    }
}