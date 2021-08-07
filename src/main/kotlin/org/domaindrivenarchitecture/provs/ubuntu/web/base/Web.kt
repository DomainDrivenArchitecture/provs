package org.domaindrivenarchitecture.provs.ubuntu.web.base

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.ProvResult
import org.domaindrivenarchitecture.provs.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.ubuntu.install.base.isPackageInstalled


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