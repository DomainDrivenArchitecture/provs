package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.*
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.base.downloadFromURL

// -----------------------------------  versions  --------------------------------

const val K9S_VERSION = "v0.32.5"

// -----------------------------------  public functions  --------------------------------


fun Prov.installK9s(): ProvResult {
    return taskWithResult {
        createDir("/tmp", sudo = true)
        downloadFromURL( "https://github.com/derailed/k9s/releases/download/" + K9S_VERSION + "/k9s_linux_amd64.deb", "k9s_linux_amd64.deb", "/tmp")
        cmd("sudo dpkg -i k9s_linux_amd64.deb", "/tmp")
    }
}
