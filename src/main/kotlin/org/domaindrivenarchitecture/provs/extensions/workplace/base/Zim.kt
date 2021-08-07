package org.domaindrivenarchitecture.provs.extensions.workplace.base

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.ProvResult
import org.domaindrivenarchitecture.provs.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.ubuntu.install.base.aptInstallFromPpa
import org.domaindrivenarchitecture.provs.ubuntu.install.base.isPackageInstalled


fun Prov.installZimWiki() = def {
    if (isPackageInstalled("zim")) {
        ProvResult(true, out = "zim already installed.")
    } else {
        aptInstallFromPpa("jaap.karssenberg", "zim", "zim")
        aptInstall("python3-gtkspellcheck")
    }
}
