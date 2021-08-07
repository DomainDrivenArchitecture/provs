package io.provs.ubuntu.install.base

import io.provs.core.Prov
import io.provs.core.ProvResult


private var aptInit = false

/**
 * Installs package(s) by using package manager "apt".
 *
 * @param packages the packages to be installed, packages separated by space if there are more than one
 */
fun Prov.aptInstall(packages: String): ProvResult = def {
    if (!aptInit) {
        cmd("sudo apt-get update")
        cmd("sudo apt-get install -qy apt-utils")
        aptInit = true
    }

    val packageList = packages.split(" ")
    for (packg in packageList) {
        // see https://superuser.com/questions/164553/automatically-answer-yes-when-using-apt-get-install
        cmd("sudo DEBIAN_FRONTEND=noninteractive apt-get install -qy $packg")
    }
    ProvResult(true) // dummy
}


/**
 * Installs a package from a ppa (personal package archive) by using package manager "apt".
 *
 * @param packageName the package to install
 */
fun Prov.aptInstallFromPpa(launchPadUser: String, ppaName: String, packageName: String): ProvResult = def {
    aptInstall("software-properties-common") // for being able to use add-apt-repository
    cmd("sudo add-apt-repository -y ppa:$launchPadUser/$ppaName")
    aptInstall(packageName)
}


/**
 * Checks if a program is installed
 *
 * @param packageName to check
 * @return true if program is installed
 */
@Suppress("unused") // used externally
fun Prov.isPackageInstalled(packageName: String): Boolean {
    return chk("timeout 2 dpkg -l $packageName")
}


/**
 * Removes a package including its configuration and data files
 */
@Suppress("unused") // used externally
fun Prov.aptPurge(packageName: String): Boolean {
    return chk("sudo apt-get purge -qy $packageName")
}
