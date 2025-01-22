package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.isPackageInstalled

fun Prov.installShadowCljs(): ProvResult = task {

    if (!isPackageInstalled("npm")) {
        aptInstall("npm")
        cmd("sudo npm install -g npx")
        cmd("sudo npm install -g shadow-cljs")
    } else {
        val npmVersion = cmd("npm --version")
        ProvResult(true, out = "Package npm v$npmVersion already installed. Checking shadow-cljs now.")
        if (chk("npm list -g shadow-cljs|grep empty")) {
            cmd("sudo npm install -g shadow-cljs")
        } else {
            ProvResult(true, out = "Package shadow-cljs already installed.")
        }
        if (chk("npm list -g npx|grep empty")) {
            cmd("sudo npm install -g npx")
        } else {
            ProvResult(true, out = "Package npx already installed.")
        }
    }
}
