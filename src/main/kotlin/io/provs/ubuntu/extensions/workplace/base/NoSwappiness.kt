package io.provs.ubuntu.extensions.workplace.base

import io.provs.Prov
import io.provs.ubuntu.filesystem.base.addTextToFile
import java.io.File

fun Prov.configureNoSwappiness() = def {
    // set swappiness to 0
    addTextToFile("vm.swappiness=0", File("/etc/sysctl.conf"), sudo = true)
}