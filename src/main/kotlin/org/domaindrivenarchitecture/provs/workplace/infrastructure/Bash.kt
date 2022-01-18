package org.domaindrivenarchitecture.provs.workplace.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.addTextToFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDir
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.dirExists
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import java.io.File


fun Prov.configureBash() = def {
    configureBashForUser()
}

fun Prov.configureBashForUser(): ProvResult = def {
    val dirname = "~/.bashrc.d"
    if(!dirExists(dirname)) {
        createDir(dirname)
        cmd("chmod 755 " + dirname)
        aptInstall("bash-completion screen")

        val enhance = """
            # source .bashrc.d files
            if [ -d ~/.bashrc.d ]; then
              for i in ~/.bashrc.d/*.sh; do
                if [ -r \$\{i} ]; then
                  . \\\$\{i}
                fi
              done
              unset i
            fi""".trimIndent() + "\n"
        addTextToFile(text = enhance, file = File("~/.bashrc"))
    } else {
        ProvResult(true)
    }
}
