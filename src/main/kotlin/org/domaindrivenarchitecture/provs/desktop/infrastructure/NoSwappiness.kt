package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.addTextToFile
import java.io.File

fun Prov.configureNoSwappiness() = task {
    // set swappiness to 0
    addTextToFile("\nvm.swappiness=0\n", File("/etc/sysctl.conf"), sudo = true)
}
