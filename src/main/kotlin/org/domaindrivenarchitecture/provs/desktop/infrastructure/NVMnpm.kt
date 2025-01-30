package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.addTextToFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.isPackageInstalledCheckCommand
import java.io.File

fun Prov.installNpmByNvm(): ProvResult = task {

    if (!isPackageInstalledCheckCommand("npm")) {
        //Node-Version-Manager from https://github.com/nvm-sh/nvm
        val versNvm = "0.40.1"
        cmd("sudo curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v$versNvm/install.sh | bash")

        cmd("chmod 755 .nvm/nvm.sh")

        addTextToFile("\n##### NVM #####\n", File("~/.bashrc"))
        addTextToFile("""export NVM_DIR="${"\$HOME/.nvm"}" """ + "\n", File("~/.bashrc"))
        addTextToFile("""[ -s "${"\$NVM_DIR/nvm.sh"}" ] && \. "${"\$NVM_DIR/nvm.sh"}" """ + "\n", File("~/.bashrc"))
        addTextToFile("""[ -s "${"\$NVM_DIR/bash_completion"}" ] && \. "${"\$NVM_DIR/bash_completion"}" """ + "\n", File("~/.bashrc"))

        cmd(". .nvm/nvm.sh && nvm install --lts")
        //to be discussed, sourcing in docker test container, separtely?
        val nvmRes = cmd(". .nvm/nvm.sh && nvm --version").toString()
        if (versNvm == nvmRes) {
            println("NVM version $versNvm")
            addResultToEval(ProvResult(true, out = "SUCCESS: NVM version $versNvm installed !!"))
        } else {
            println("FAIL: NVM version $versNvm is not installed !!")
            addResultToEval(ProvResult(true, out = "FAIL: NVM version $versNvm is not installed !!"))
        }
        cmd(". .nvm/nvm.sh && node -v")
        cmd(". .nvm/nvm.sh && npm --version")
     } else {
        ProvResult(true, out = "npm already installed")
    }


}