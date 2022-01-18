package org.domaindrivenarchitecture.provs.workplace.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.addTextToFile
import java.io.File

fun Prov.configureNoSwappiness() = def {
    // set swappiness to 0
    addTextToFile("vm.swappiness=0", File("/etc/sysctl.conf"), sudo = true)
}