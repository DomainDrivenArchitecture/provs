package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDirs
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.base.downloadFromURL

fun Prov.installBinariesC4k() = task {

    val installationPath = " /usr/local/bin/"

    createDirs(installationPath, sudo = true)

    class Binary(val jobId: String, val fileName: String, val sha256sum: String)

    fun downloadC4k(bin : Binary) {
        with(bin) {
            downloadFromURL(
                "https://gitlab.com/domaindrivenarchitecture/c4k-nextcloud/-/jobs/${jobId}/artifacts/raw/target/uberjar/${fileName}",
                path = installationPath,
                filename = fileName,
                sha256sum = sha256sum,
                sudo = true
            )
            cmd("chmod 755 $installationPath$fileName", sudo = true)
        }

    }
    // check for latest stable release on: https://gitlab.com/domaindrivenarchitecture/c4k-nextcloud/-/releases
    // release 1.0.2
    val nextcloud = Binary("1824231745", "c4k-nextcloud-standalone.jar", "5b7eeecf745c720184be4fccdd61a49509ae5e322558506eb6bc3c3ed680c23f")

    // release 1.0.6

    ProvResult(true)

}