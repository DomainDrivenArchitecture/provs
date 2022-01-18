package org.domaindrivenarchitecture.provs.framework.ubuntu.web.base

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.core.tags.Api
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDirs
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.deleteFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall


/**
 * Downloads a file from the given URL using curl.
 *
 * ATTENTION: to check the checksum the locally installed version of sha256sum is used, which can differ in different versions of ubuntu; e.g. gopass download only works with sha256sum version 8.30 from ubuntu 20.04 !
 */
@Api
fun Prov.downloadFromURL(
    url: String,
    filename: String? = null,
    path: String? = null,
    sudo: Boolean = false,
    followRedirect: Boolean = true,
    sha256sum: String? = null
): ProvResult = def {

    aptInstall("curl")

    val followRedirectOption = if (followRedirect) "-L" else ""
    val filenameFromUrl = url.substringAfterLast("/")

    val finalFilename: String = filename ?: filenameFromUrl

    path?.let { createDirs(path) }
    cmd("curl $followRedirectOption $url -o $finalFilename", path, sudo)

    if (sha256sum != null) {
        cmd("sha256sum --version")  // log version (e.g. 8.30 for ubuntu 20.04)
        if (!cmd("echo \"$sha256sum $finalFilename\" | sha256sum --check", path).success) {
            cmd("sha256sum $finalFilename", path)  // log the actual checksum
            deleteFile(finalFilename, path, sudo)
        } else {
            ProvResult(true, out = "Sha256sum is correct.")
        }
    } else {
        ProvResult(true, out = "No sha256sum check requested.")
    }
}