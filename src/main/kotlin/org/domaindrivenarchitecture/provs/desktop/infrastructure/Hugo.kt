package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.deleteFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.userHome
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptPurge
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.base.downloadFromURL

// see https://github.com/gohugoio/hugo/releases/
fun Prov.installHugoByDeb() = task {
    val sha256sum = "e72b3c374348240cfb21cf16a395d8722505b5ff3b1742012b9b3d0a53eaa886"
    val version = "0.143.0"
    val filename = "hugo_extended_${version}_linux-amd64.deb"
    val downloadUrl = "-L https://github.com/gohugoio/hugo/releases/download/v$version/$filename"
    val downloadDir = "${userHome()}Downloads"
    val currentHugoVersion = cmdNoEval("hugo version").out ?: ""

    if (needsHugoInstall(currentHugoVersion, version)) {
        if (isHugoInstalled(currentHugoVersion)) {
            if (currentHugoVersion.contains("snap")) {
                cmd("snap remove hugo", sudo = true)
            } else {
                aptPurge("hugo")
            }
        }
        aptInstall("gnupg2")
        downloadFromURL(downloadUrl, filename, downloadDir, sha256sum = sha256sum)
        cmd("dpkg -i $downloadDir/$filename", sudo = true)
        deleteFile("$downloadDir/$filename")
    }
}

fun needsHugoInstall(currentHugoVersion: String?, requiredHugoVersion: String) : Boolean {
    if (currentHugoVersion == null) {
        return true
    }
    if (!isHugoInstalled(currentHugoVersion)) {
        return true
    }
    if (!isHugoExtended(currentHugoVersion)) {
        return true
    }
    if (isLowerHugoVersion(requiredHugoVersion, currentHugoVersion)) {
        return true
    }
    return false
}

fun isHugoInstalled(hugoVersion: String?) : Boolean {
    if (hugoVersion == null) {
        return false
    }
    return hugoVersion.contains("hugo")
}

fun isHugoExtended(hugoVersion: String) : Boolean {
    return hugoVersion.contains("extended")
}

fun isLowerHugoVersion(requiredHugoVersion: String, currentHugoVersion: String ) : Boolean {
    val reqVersionNo = getHugoVersionNo(requiredHugoVersion)
    val currentVersionNo = getHugoVersionNo(currentHugoVersion)
    return when {
        compareVersions(currentVersionNo, reqVersionNo).contains("lower") -> true
        else -> false
    }
}

fun compareVersions(firstVersion : List<Int>, secondVersion: List<Int>) : String {
    var result = ""
    for (i in 0..2) {
        when {
            firstVersion[i] > secondVersion[i] -> result += " higher"
            firstVersion[i] < secondVersion[i] -> result += " lower"
            firstVersion[i] == secondVersion[i] -> result += " equal"
        }
    }
    return result
}

/**
 * Parses hugo version.
 * @param hugoVersion can either be version a simple version like "1.22.33" or
 * a version string like: hugo v0.126.1-3d40ab+extended linux/amd64 BuildDate=2024-05-15T10:42:34Z VendorInfo=snap:0.126.1
 */
fun getHugoVersionNo(hugoVersion: String) : List<Int> {
    val words = hugoVersion.split(" ")
    val result = if (words.size > 1) words[1] else words[0]
    val versionString = result.split("-")[0].removePrefix("v")
    return versionString.split(".").map { it.toInt() }
}
