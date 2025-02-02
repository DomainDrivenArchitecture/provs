package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult

/**
 * Installs ShadowCljs. Prerequisite: NVM and NPM are installed, otherwise fails.
 */
fun Prov.installShadowCljs(): ProvResult = task {

    if (!chk(". .nvm/nvm.sh")) {
        addResult(false, err = "nvm not installed!")
    } else {
        if (!chk("npm list -g --depth=0 | grep shadow-cljs")) {
            cmd(". .nvm/nvm.sh && npm install -g shadow-cljs")
        }
    }
}
