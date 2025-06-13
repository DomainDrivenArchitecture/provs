package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.core.getResourceAsText
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.addTextToFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.createDir
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.checkDir
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.infrastructure.aptInstall
import java.io.File

private val resourcePath = "org/domaindrivenarchitecture/provs/desktop/infrastructure/"

fun Prov.configureBash() = task {
    configureBashForUser()
}

fun Prov.configureBashForUser(): ProvResult = task {
    val dirname = "~/.bashrc.d"
    if(!checkDir(dirname)) {
        createDir(dirname)
        cmd("chmod 755 " + dirname)
        aptInstall("bash-completion screen")

        val enhance = getResourceAsText(resourcePath + "bashrcd-enhancement.sh").trimIndent() + "\n"
        addTextToFile(text = enhance, file = File("~/.bashrc"))
    } else {
        ProvResult(true)
    }
}
