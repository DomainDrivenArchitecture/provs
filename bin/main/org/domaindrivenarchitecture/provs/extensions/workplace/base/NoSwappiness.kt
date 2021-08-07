package org.domaindrivenarchitecture.provs.extensions.workplace.base

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.addTextToFile
import java.io.File

fun Prov.configureNoSwappiness() = def {
    // set swappiness to 0
    addTextToFile("vm.swappiness=0", File("/etc/sysctl.conf"), sudo = true)
}