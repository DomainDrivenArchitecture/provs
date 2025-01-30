package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.isPackageInstalled

fun Prov.installShadowCljs(): ProvResult = task {

    if (!isPackageInstalled("shadow-cljs")) {
        cmd(". .nvm/nvm.sh && npm install -g shadow-cljs")
        cmd(". .nvm/nvm.sh && shadow-cljs --help")
    } else {
        ProvResult(true, out = "shadow-cljs already installed")
    }
}
