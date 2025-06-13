package org.domaindrivenarchitecture.provs.framework.ubuntu.web.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.checkFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.createDirs
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.deleteFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.normalizePath
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.infrastructure.aptInstall
import java.util.regex.Pattern


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

    val finalFilename: String = filename ?: url.substringAfterLast("/")
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
    val ipText = cmd("dig +short $hostname").out?.trim() ?: ""

    // check if ipText is valid
    return if (isIp4(ipText) || isIp6(ipText)) ipText else null
}

/**
 * Returns the ip for the given hostname if found else throws a RuntimeExeption
 */
fun Prov.getIpForHostname(hostname: String): String {
    return findIpForHostname(hostname) ?: throw RuntimeException("Could not resolve ip for: $hostname")
}


internal fun isIp4(ip: String): Boolean {
    val IPV4_PATTERN = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)(\\.(?!\$)|\$)){4}\$"

    val pattern = Pattern.compile(IPV4_PATTERN)
    val matcher = pattern.matcher(ip)
    return matcher.matches()
}

internal fun isIp6(ip: String): Boolean {
    val IPV6_PATTERN = "(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}" +
            "|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}" +
            "|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)" +
            "|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]" +
            "|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))"

    val pattern = Pattern.compile(IPV6_PATTERN)
    val matcher = pattern.matcher(ip)
    return matcher.matches()
}
