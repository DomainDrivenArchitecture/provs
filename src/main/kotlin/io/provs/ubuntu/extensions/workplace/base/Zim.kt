package io.provs.ubuntu.extensions.workplace.base

import io.provs.Prov
import io.provs.ProvResult
import io.provs.ubuntu.install.base.aptInstall
import io.provs.ubuntu.install.base.aptInstallFromPpa
import io.provs.ubuntu.install.base.isPackageInstalled


fun Prov.installZimWiki() = def {
    if (isPackageInstalled("zim")) {
        ProvResult(true, out = "zim already installed.")
    } else {
        aptInstallFromPpa("jaap.karssenberg", "zim", "zim")
        aptInstall("python3-gtkspellcheck")
    }
}
