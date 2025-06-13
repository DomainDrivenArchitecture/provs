package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.infrastructure.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.infrastructure.aptInstallFromPpa
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.infrastructure.checkPackage


fun Prov.installZimWiki() = task {
    if (checkPackage("zim")) {
        ProvResult(true, out = "zim already installed.")
    } else {
        aptInstallFromPpa("jaap.karssenberg", "zim", "zim")
        aptInstall("python3-gtkspellcheck")
    }
}
