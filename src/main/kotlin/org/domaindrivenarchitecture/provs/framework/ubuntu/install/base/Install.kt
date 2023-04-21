package org.domaindrivenarchitecture.provs.framework.ubuntu.install.base

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult


private var aptInit = false

/**
 * Installs package(s) by using package manager "apt".
 *
 * @param packages the packages to be installed, packages must be separated by space if there are more than one
 * @param ignoreAlreadyInstalled if true, then for an already installed package no action will be taken,
 * if "ignoreAlreadyInstalled" is false, then installation is always attempted, which normally results in an upgrade if package wa already installed
 */
@Suppress("UNUSED_PARAMETER")  // ignoreAlreadyInstalled is added for external usage
fun Prov.aptInstall(packages: String, ignoreAlreadyInstalled: Boolean = true): ProvResult = taskWithResult {
    val packageList = packages.split(" ")
    val allInstalled: Boolean = packageList.map { isPackageInstalled(it) }.fold(true, { a, b -> a && b })
    if (!allInstalled) {
        if (!isPackageInstalled(packages)) {
            if (!aptInit) {
                cmd("sudo apt-get update")
                cmd("sudo apt-get install -qy apt-utils")
                aptInit = true
            }
        }

        for (packg in packageList) {
            // see https://superuser.com/questions/164553/automatically-answer-yes-when-using-apt-get-install
            cmd("sudo DEBIAN_FRONTEND=noninteractive apt-get install -qy $packg")
        }
        ProvResult(true) // dummy
    } else {
        ProvResult(true, out = "All packages are already installed. [$packages]")
    }
}


/**
 * Installs package(s) by using package manager "snap".
 *
 * @param packages the packages to be installed, packages must be separated by space if there are more than one
 */
@Suppress("unused")   // api - for external usage
fun Prov.snapInstall(packages: String, classic: Boolean = false): ProvResult = task {
    val packageList = packages.split(" ")
    for (pkg in packageList) {
        cmd("sudo snap install $pkg" + if (classic) " --classic" else "")
    }
    ProvResult(true) // dummy
}


/**
 * Installs a package from a ppa (personal package archive) by using package manager "apt".
 *
 * @param packageName the package to install
 */
fun Prov.aptInstallFromPpa(launchPadUser: String, ppaName: String, packageName: String): ProvResult = task {
    aptInstall("software-properties-common") // for being able to use add-apt-repository
    cmd("sudo add-apt-repository -y ppa:$launchPadUser/$ppaName")
    aptInstall(packageName)
}


/**
 * Returns true if a package is installed else false
 */
fun Prov.isPackageInstalled(packageName: String): Boolean {
    return chk("dpkg -s $packageName")
}


/**
 * Returns true if a package is installed else false
 */
fun Prov.checkPackageInstalled(packageName: String): ProvResult = taskWithResult {
    cmd("dpkg -s $packageName")
}


/**
 * Removes a package including its configuration and data file
 */
@Suppress("unused") // used externally
fun Prov.aptPurge(packageName: String): Boolean {
    return chk("sudo apt-get purge -qy $packageName")
}
