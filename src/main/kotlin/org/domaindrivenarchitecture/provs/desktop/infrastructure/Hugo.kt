package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.userHome
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptPurge
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.base.downloadFromURL

fun Prov.installHugoByDeb() = task {
    val sha256sum = "46692ac9b79d5bc01b0f847f6dcf651d8630476de63e598ef61a8da9461d45cd"
    val requiredHugoVersion = "0.125.5"
    val filename = "hugo_extended_0.125.5_linux-amd64.deb"
    val downloadUrl = "-L https://github.com/gohugoio/hugo/releases/download/v$requiredHugoVersion/$filename"
    val downloadDir = "${userHome()}Downloads"
    val currentHugoVersion = cmdNoEval("hugo version").out ?: ""

    if (needsHugoInstall(currentHugoVersion, requiredHugoVersion)) {
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
    var words = hugoVersion.split(" ")
    var result = if (words.size > 1) words[1] else words[0]
    result = result.split("-")[0].removePrefix("v")
    return result.split(".").map { it.toInt() }
}
