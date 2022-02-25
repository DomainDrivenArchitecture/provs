package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDirs
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.base.downloadFromURL


fun Prov.installBinariesProvs() = task {
    // check for latest stable release on: https://gitlab.com/domaindrivenarchitecture/provs/-/releases
    // release 0.9.6
    val jobId = "2083873496"
    val installationPath = " /usr/local/bin/"
    val provsServerSha256sum = "1127d0a939e1d3eec8e15cd3969f565732b89d9ca10bbaf134840d25aeb3f03b"
    val provsDesktopSha256sum = "626f1e01fca5845a54ddd1e645e52bb4b05d04a4cfa060cd18f1ad15a5d387ad"

    createDirs(installationPath, sudo = true)

    downloadFromURL(
        "https://gitlab.com/domaindrivenarchitecture/provs/-/jobs/$jobId/artifacts/raw/build/libs/provs-server.jar",
        path = installationPath,
        filename = "provs-server.jar",
        sha256sum = provsServerSha256sum,
        sudo = true
    )

    downloadFromURL(
        "https://gitlab.com/domaindrivenarchitecture/provs/-/jobs/$jobId/artifacts/raw/build/libs/provs-desktop.jar",
        path = installationPath,
        filename = "provs-desktop.jar",
        sha256sum = provsDesktopSha256sum,
        sudo = true
    )

    cmd("chmod 755 /usr/local/bin/provs-server.jar" , sudo = true)
    cmd("chmod 755  /usr/local/bin/provs-desktop.jar", sudo = true)
}
