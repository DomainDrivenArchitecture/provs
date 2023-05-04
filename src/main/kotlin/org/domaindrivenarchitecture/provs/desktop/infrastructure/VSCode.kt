package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.isPackageInstalled


fun Prov.installVSC(vararg options: String) = task {
    val clojureExtensions =
        arrayListOf("betterthantomorrow.calva", "martinklepsch.clojure-joker-linter", "DavidAnson.vscode-markdownlint")
    val pythonExtensions = arrayListOf("ms-python.python")

    prerequisitesVSCinstall()

    installVSCPackage()
    installVSCodiumPackage()

    if (options.contains("clojure")) {
        installExtensionsCode(clojureExtensions)
        installExtensionsCodium(clojureExtensions)
    }
    if (options.contains("python")) {
        installExtensionsCode(pythonExtensions)
        installExtensionsCodium(pythonExtensions)
    }

    provisionAdditionalToolsForVSCode()
}


private fun Prov.prerequisitesVSCinstall() = task {
    aptInstall("curl gpg unzip apt-transport-https")
}


@Suppress("unused") // only required for installation of vscode via apt
private fun Prov.installVscWithApt() = task {
    val packageName = "code"
    if (!isPackageInstalled(packageName)) {
        // see https://code.visualstudio.com/docs/setup/linux
        // alternatively install with snapd (but this cannot be tested within docker as snapd within docker is not working/supported)
        sh("""
            curl https://packages.microsoft.com/keys/microsoft.asc | gpg --dearmor > packages.microsoft.gpg
            sudo install -o root -g root -m 644 packages.microsoft.gpg /etc/apt/trusted.gpg.d/
            sudo sh -c 'echo \"deb [arch=amd64 signed-by=/etc/apt/trusted.gpg.d/packages.microsoft.gpg] https://packages.microsoft.com/repos/vscode stable main\" > /etc/apt/sources.list.d/vscode.list'
        """)
        aptInstall("apt-transport-https")
        aptInstall(packageName)
    } else {
        ProvResult(true, out = "Package $packageName already installed.")
    }
}


private fun Prov.installVSCPackage() = task {
    cmd("sudo snap install code --classic")

    // to install via apt use:
    //    installVscWithApt()
}

private fun Prov.installVSCodiumPackage() = task {
    cmd("sudo snap install codium --classic")
}


private fun Prov.installExtensionsCode(extensions: List<String>) = optional {
    var res = ProvResult(true)
    for (ext in extensions) {
        res = cmd("code --install-extension $ext")
    }
    res
    // Settings can be found at $HOME/.config/Code/User/settings.json
}

private fun Prov.installExtensionsCodium(extensions: List<String>) = optional {
    var res = ProvResult(true)
    for (ext in extensions) {
        res = cmd("codium --install-extension $ext")
    }
    res
    // Settings can be found at $HOME/.config/Code/User/settings.json
}


internal fun Prov.provisionAdditionalToolsForVSCode() = task {
    // Joker
    val version = "1.1.0"
    cmd("curl -Lo joker-${version}-linux-amd64.zip https://github.com/candid82/joker/releases/download/v${version}/joker-${version}-linux-amd64.zip")
    cmd("unzip joker-${version}-linux-amd64.zip")
    cmd("sudo mv joker /usr/local/bin/")
}
