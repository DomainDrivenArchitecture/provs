package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDirs
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.base.downloadFromURL


fun Prov.installBinariesProvs(reprovision: Boolean = false) = task {
    // check for latest stable release on: https://gitlab.com/domaindrivenarchitecture/provs/-/releases
    // release 0.9.8
    val jobId = "2137287031"
    val installationPath = " /usr/local/bin/"
    val provsDesktopSha256sum = "8ad8ca69adf9b3da0a56d088a8694b366528fdcb4ad6b5047b42f32c4877d7ce"
    val provsServerSha256sum = "91adf9bf6bad18b891eed53e23ad03182824daf3c724599255ee8a56294bf88c"

    createDirs(installationPath, sudo = true)

    downloadFromURL(
        "https://gitlab.com/domaindrivenarchitecture/provs/-/jobs/$jobId/artifacts/raw/build/libs/provs-desktop.jar",
        path = installationPath,
        filename = "provs-desktop.jar",
        sha256sum = provsDesktopSha256sum,
        sudo = true,
        overwrite = reprovision
    )
    cmd("chmod 755 /usr/local/bin/provs-desktop.jar", sudo = true)

    downloadFromURL(
        "https://gitlab.com/domaindrivenarchitecture/provs/-/jobs/$jobId/artifacts/raw/build/libs/provs-server.jar",
        path = installationPath,
        filename = "provs-server.jar",
        sha256sum = provsServerSha256sum,
        sudo = true,
        overwrite = reprovision
    )
    cmd("chmod 755 /usr/local/bin/provs-server.jar" , sudo = true)
}
