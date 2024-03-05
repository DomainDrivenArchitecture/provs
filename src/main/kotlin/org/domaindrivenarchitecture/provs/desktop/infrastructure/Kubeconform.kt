package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDirs
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.base.downloadFromURL


fun Prov.installKubeconform() = task {
    // check for latest stable release on: https://github.com/yannh/kubeconform/releases
    val version = "0.6.4"
    val installationPath = "~/bin/"
    val filename = "kubeconform-linux-amd64"
    val packedFilename = "$filename.tar.gz"

    createDirs(installationPath)

    downloadFromURL(
        "https://github.com/yannh/kubeconform/releases/download/v$version/$packedFilename",
        path = installationPath,
        sha256sum = "2b4ebeaa4d5ac4843cf8f7b7e66a8874252b6b71bc7cbfc4ef1cbf85acec7c07"
    )
    cmd("tar -xvf $packedFilename", installationPath)
    cmd("chmod +x kubeconform", installationPath)
}


