package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstallFromPpa
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.isPackageInstalled


fun Prov.installZimWiki() = task {
    if (isPackageInstalled("zim")) {
        ProvResult(true, out = "zim already installed.")
    } else {
        aptInstallFromPpa("jaap.karssenberg", "zim", "zim")
        aptInstall("python3-gtkspellcheck")
    }
}
