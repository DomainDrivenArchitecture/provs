package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.userHome

/**
 * Installs Node.js and NPM (Node Package Manager) by using NVM (Node Version Manager), if NVM not already installed
 */
fun Prov.installNpmByNvm(version: String = "0.40.1"): ProvResult = task {

    //see Node-Version-Manager at https://github.com/nvm-sh/nvm

    val bashConfigFile = "~/.bashrc.d/npmbynvm.sh"
    if (!checkFile(".nvm/nvm.sh") && !checkFile(bashConfigFile)) {

        // install NVM
        cmd("sudo curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v$version/install.sh | bash")
        cmd("chmod 755 .nvm/nvm.sh")

        // configure bash - create file ".bashrc.d/npmbynvm.sh" with settings
        configureBashForUser()

        val content = """
            export NVM_DIR="${userHome()}.nvm"
            [ -s "${"\$NVM_DIR/nvm.sh"}" ] && \. "${"\$NVM_DIR/nvm.sh"}"
            """.trimIndent()
        createFile(bashConfigFile, content)

        // install Node.js and NPM
        cmd(". .nvm/nvm.sh && nvm install --lts")
    }
}