package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.isPackageInstalled

fun Prov.installShadowCljs(): ProvResult = task {

    if (!isPackageInstalled("npm")) {
        // installation of npm is too chatty even with quite install and will hang when using Java ProcessBuilder => install with output must be ignored
        optional {
            // may fail for some packages, but this should in general not be an issue
            cmd("sudo apt-get update")
        }
        cmd("sudo apt-get install -qy apt-utils")
        cmd("sudo DEBIAN_FRONTEND=noninteractive apt-get install -qy npm > /dev/null")
        cmd("sudo npm install -g npx")
        cmd("sudo npm install -g shadow-cljs")
    } else {
        val npmVersion = cmd("npm --version")
        addResultToEval(ProvResult(true, out = "Package npm v$npmVersion already installed. Checking shadow-cljs now."))
        if (chk("npm list -g shadow-cljs | grep empty")) {
            cmd("sudo npm install -g shadow-cljs")
        } else {
            addResultToEval(ProvResult(true, out = "Package shadow-cljs already installed."))
        }
        if (chk("npm list -g npx|grep empty")) {
            cmd("sudo npm install -g npx")
        } else {
            addResultToEval(ProvResult(true, out = "Package npx already installed."))
        }
    }
}
