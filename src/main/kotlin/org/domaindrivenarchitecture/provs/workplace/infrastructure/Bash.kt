package org.domaindrivenarchitecture.provs.workplace.infrastructure

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.ProvResult
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.*
import org.domaindrivenarchitecture.provs.ubuntu.install.base.aptInstall
import java.io.File


fun Prov.installBash() = def {
    installBashForUser()
}

fun Prov.installBashForUser(): ProvResult = def {
    var dirname = "~/.bashrd.d"
    if(!dirExists(dirname)) {
        createDir(dirname)
        cmd("chmod 755 " + dirname)
        aptInstall("bash-completion screen")

        var enhance = """
            # source .bashrc.d files
            if [ -d ~/.bashrc.d ]; then
              for i in ~/.bashrc.d/*.sh; do
               if [ -r \$\{i} ]; then
                  . \\\$\{i}
                fi
              done
              unset i
            fi """.trimIndent()
        addTextToFile(text = enhance, file = File("~/.bashrc"))
    } else {
        ProvResult(true)
    }
}
