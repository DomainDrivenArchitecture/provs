package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.core.getResourceAsText
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.addTextToFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDir
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.dirExists
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import java.io.File

private val resourcePath = "org/domaindrivenarchitecture/provs/desktop/infrastructure/"

fun Prov.configureBash() = task {
    configureBashForUser()
}

fun Prov.configureBashForUser(): ProvResult = task {
    val dirname = "~/.bashrc.d"
    if(!dirExists(dirname)) {
        createDir(dirname)
        cmd("chmod 755 " + dirname)
        aptInstall("bash-completion screen")

        val enhance = getResourceAsText(resourcePath + "bashrcd-enhancement.sh").trimIndent() + "\n"
        addTextToFile(text = enhance, file = File("~/.bashrc"))
    } else {
        ProvResult(true)
    }
}
