package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.base.downloadFromURL


const val K9S_VERSION = "v0.32.5"


fun Prov.installK9s() = task {
    if (cmdNoEval("k9s version").out?.contains(K9S_VERSION) != true) {
        downloadFromURL("https://github.com/derailed/k9s/releases/download/$K9S_VERSION/k9s_linux_amd64.deb", "k9s_linux_amd64.deb", "/tmp")
        cmd("sudo dpkg -i k9s_linux_amd64.deb", "/tmp")
    }
}
