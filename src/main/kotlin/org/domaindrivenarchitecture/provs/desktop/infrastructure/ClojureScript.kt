package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall

fun Prov.installShadowCljs(): ProvResult = task {
    aptInstall("npm")
    cmd("npm install -g npx", sudo = true)
    cmd("npm install -g shadow-cljs", sudo = true)
}
