package org.domaindrivenarchitecture.provs.framework.ubuntu.web.base

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDirs
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.deleteFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.normalizePath
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.UnknownHostException


/**
 * Downloads a file from the given URL using curl. Skips download if file already exists and overwrite is false.
 *
 * ATTENTION: to check the checksum the locally installed version of sha256sum is used, which can differ in different versions of ubuntu; e.g. gopass download only works with sha256sum version 8.30 from ubuntu 20.04 !
 */
fun Prov.downloadFromURL(
    url: String,
    filename: String? = null,
    path: String? = null,
    sudo: Boolean = false,
    followRedirect: Boolean = true,
    sha256sum: String? = null,
    overwrite: Boolean = false
): ProvResult = taskWithResult {

    val finalFilename: String = filename ?:  url.substringAfterLast("/")
    val fqFilename: String = (path?.normalizePath() ?: "") + finalFilename

    if (!overwrite && checkFile(fqFilename, sudo = sudo)) {
        return@taskWithResult ProvResult(true, out = "File $fqFilename already exists.")
    }

    aptInstall("curl")

    val followRedirectOption = if (followRedirect) "-L" else ""

    path?.let { createDirs(path) }
    cmd("curl $followRedirectOption $url -o $finalFilename", path, sudo)

    if (sha256sum != null) {
        cmd("sha256sum --version")  // use cmd to log version (e.g. 8.30 for ubuntu 20.04)
        if (!cmd("echo \"$sha256sum $finalFilename\" | sha256sum --check", path).success) {

            // use cmd to log the actual checksum
            cmd("sha256sum $finalFilename", path)

            // delete file with wrong checksum
            deleteFile(finalFilename, path, sudo)
        } else {
            ProvResult(true, out = "Sha256sum is correct.")
        }
    } else {
        ProvResult(true, out = "No sha256sum check requested.")
    }
}


/**
 * Returns the ip for the given hostname if found else null
 */
fun Prov.findIpForHostname(hostname: String): String? {
    val ipText = cmd("dig +short $hostname").out?.trim()

    // check if ipText is valid
    return try {
        val ip = InetAddress.getByName(ipText ?: "")
        return if (ip is Inet4Address || ip is Inet6Address) ipText else null
    } catch (exception: UnknownHostException) {
        null
    }
}

/**
 * Returns the ip for the given hostname if found else throws a RuntimeExeption
 */
fun Prov.getIpForHostname(hostname: String): String {
    return findIpForHostname(hostname) ?: throw RuntimeException("Could not resolve ip for: $hostname")
}
