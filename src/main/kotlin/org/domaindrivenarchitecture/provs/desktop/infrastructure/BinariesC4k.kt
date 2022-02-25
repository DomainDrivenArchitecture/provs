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
    downloadC4k(Binary("2138015489", "c4k-nextcloud-standalone.jar",
        "47b6f71664903816eab0e4950d4ab0d564ea365ddb2b9c2a7bcb2736a2d941cb"))
    downloadC4k(Binary("2136806347", "c4k-jira-standalone.jar",
        "585f26e3b70bec32f052f488688718d8c9a6d15222b8a2141a69fa1bea136179"))
    downloadC4k(Binary("2136561868", "c4k-keycloak-standalone.jar",
        "81bd605b160c3d339cba745433271412eaa716cb7856d0227a1eb063badc9"))
    downloadC4k(Binary("2136778473", "c4k-mastodon-bot-standalone.jar",
        "9dbf981ce8b4aea92e0f45b182a39d2caffceb6e8f78ddfeeb49e74ca4a8d37d"))
    downloadC4k(Binary("2136801572", "c4k-shynet-standalone.jar",
        "4fa0d41896f2a9ea89ca70c475f9d1f89edf3fadf82d6b39789b90732f795429"))

    ProvResult(true)

}