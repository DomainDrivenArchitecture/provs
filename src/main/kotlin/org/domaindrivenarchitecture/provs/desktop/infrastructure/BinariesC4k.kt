package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDirs
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.base.downloadFromURL

fun Prov.installBinariesC4k(reprovision: Boolean = false) = task {

    val installationPath = " /usr/local/bin/"

    createDirs(installationPath, sudo = true)

    fun Prov.downloadC4k(jobId: String, c4kName: String, sha256sum: String) = task {
        val fileName = "$c4kName-standalone.jar"
        downloadFromURL(
            "https://gitlab.com/domaindrivenarchitecture/$c4kName/-/jobs/${jobId}/artifacts/raw/target/uberjar/${fileName}",
            path = installationPath,
            filename = fileName,
            sha256sum = sha256sum,
            sudo = true,
            overwrite = reprovision
        )
        // remark: chmod fails if file could not be downloaded (e.g. due to wrong link or checksum)
        cmd("chmod 755 $installationPath$fileName", sudo = true)
    }

    downloadC4k(
        "2138015489",
        "c4k-nextcloud",
        "47b6f71664903816eab0e4950d4ab0d564ea365ddb2b9c2a7bcb2736a2d941cb"
    )
    downloadC4k(
        "2136806347",
        "c4k-jira",
        "585f26e3b70bec32f052f488688718d8c9a6d15222b8a2141a69fa1bea136179"
    )
    downloadC4k(
        "2136561868",
        "c4k-keycloak",
        "81bd605b160c3d339cba745433271412eaa716cb7856d0227a1eb063badc9d9c"
    )
    downloadC4k(
        "2136778473",
        "c4k-mastodon-bot",
        "9dbf981ce8b4aea92e0f45b182a39d2caffceb6e8f78ddfeeb49e74ca4a8d37d"
    )
    downloadC4k(
        "2136801572",
        "c4k-shynet",
        "4fa0d41896f2a9ea89ca70c475f9d1f89edf3fadf82d6b39789b90732f795429"
    )

    ProvResult(true)

}