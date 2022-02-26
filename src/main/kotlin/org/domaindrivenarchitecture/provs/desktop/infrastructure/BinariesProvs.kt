package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDirs
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.base.downloadFromURL


fun Prov.installBinariesProvs(reprovision: Boolean = false) = task {
    // check for latest stable release on: https://gitlab.com/domaindrivenarchitecture/provs/-/releases
    // release 0.9.9
    val jobId = "2138969146"
    val installationPath = " /usr/local/bin/"
    val provsDesktopSha256sum = "e309ea598234c5128095d554dad569fcad26e054431cdebbcd50b7c40ee5276f"
    val provsServerSha256sum = "659e3d8c08166288aa5c376194f28e19cbc401edbd9af4225d76c0880bc8518e"

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
