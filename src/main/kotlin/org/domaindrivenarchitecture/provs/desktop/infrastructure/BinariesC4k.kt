package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDirs
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.base.downloadFromURL

fun Prov.installBinariesC4k() = task {
    // check for latest stable release on: https://gitlab.com/domaindrivenarchitecture/c4k-nextcloud/-/releases
    // release 1.0.2
    val jobId = "1824231745"
    val installationPath = " /usr/local/bin/"
    val fileName = "c4k-nextcloud-standalone.jar"
    val c4kNextcloudSha256sum = "5b7eeecf745c720184be4fccdd61a49509ae5e322558506eb6bc3c3ed680c23f"

    createDirs(installationPath, sudo = true)

    downloadFromURL(
        "https://gitlab.com/domaindrivenarchitecture/c4k-nextcloud/-/jobs/$jobId/artifacts/raw/target/uberjar/$fileName",
        path = installationPath,
        filename = fileName,
        sha256sum = c4kNextcloudSha256sum,
        sudo = true
    )
    cmd("chmod 755 $installationPath$fileName", sudo = true)

}