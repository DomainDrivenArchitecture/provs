package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.userHome
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.isPackageInstalledCheckCommand

fun Prov.installNpmByNvm(): ProvResult = task {

    if (!isPackageInstalledCheckCommand("npm")) {
        //Node-Version-Manager from https://github.com/nvm-sh/nvm
        val version = "0.40.1"
        cmd("sudo curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v$version/install.sh | bash")

        cmd("chmod 755 .nvm/nvm.sh")

        configureBashForUser()
        createFile("~/.bashrc.d/npmbynvm.sh", """export NVM_DIR="${userHome()}.nvm" """ + "\n"
        + """[ -s "${"\$NVM_DIR/nvm.sh"}" ] && \. "${"\$NVM_DIR/nvm.sh"}" """ + "\n")

        cmd(". .nvm/nvm.sh && nvm install --lts")

        val nvmRes = cmd(". .nvm/nvm.sh && nvm --version").toString()
        if (version == nvmRes) {
            println("NVM version $version")
            addResultToEval(ProvResult(true, out = "SUCCESS: NVM version $version installed !!"))
        } else {
            println("FAIL: NVM version $version is not installed !!")
            addResultToEval(ProvResult(true, out = "FAIL: NVM version $version is not installed !!"))
        }
        cmd(". .nvm/nvm.sh && node -v")
        cmd(". .nvm/nvm.sh && npm --version")

    } else {
        addResultToEval(ProvResult(true, out = "npm already installed"))
    }


}