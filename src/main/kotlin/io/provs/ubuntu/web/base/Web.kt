package io.provs.ubuntu.web.base

import io.provs.Prov
import io.provs.ProvResult
import io.provs.ubuntu.install.base.aptInstall
import io.provs.ubuntu.install.base.isPackageInstalled


/**
 * Downloads a file from the given URL using curl
 *
 * @param path where to download to
 * @param url file to download
 * @param filename filename after download
 */
@Suppress("unused") // used externally
fun Prov.downloadFromURL(url: String, filename: String? = null, path: String? = null, sudo: Boolean = false) : ProvResult = def {

    if (!isPackageInstalled("curl")) aptInstall("curl")

    if (filename == null) {
        cmd("curl $url", path, sudo)
    } else {
        cmd("curl $url -o $filename", path, sudo)
    }
}