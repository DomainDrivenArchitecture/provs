package io.provs.platforms

import io.provs.Prov
import io.provs.ProvResult
import io.provs.processors.LocalProcessor
import io.provs.processors.Processor


class WinProv internal constructor(processor : Processor = LocalProcessor(), name: String? = null) : Prov (processor, name) {

    // todo put cmd.exe in variable SHELL

    override fun cmd(cmd: String, dir: String?) : ProvResult = def {
        xec("cmd.exe", "/c", if (dir == null) cmd else "cd $dir && $cmd")
    }

    override fun cmdNoLog(cmd: String, dir: String?) : ProvResult = def {
        xecNoLog("cmd.exe", "/c", if (dir == null) cmd else "cd $dir && $cmd")
    }


    override fun cmdNoEval(cmd: String, dir: String?) : ProvResult {
        return xec("cmd.exe", "/c", if (dir == null) cmd else "cd $dir && $cmd")
    }
}