package org.domaindrivenarchitecture.provs.workplace.infrastructure

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.ProvResult
import org.domaindrivenarchitecture.provs.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.ubuntu.install.base.isPackageInstalled


fun Prov.installVSC(vararg options: String) = requireAll {
    val clojureExtensions =
        arrayListOf("betterthantomorrow.calva", "martinklepsch.clojure-joker-linter", "DavidAnson.vscode-markdownlint")
    val pythonExtensions = arrayListOf("ms-python.python")

    prerequisitesVSCinstall()

    installVSCPackage()

    if (options.contains("clojure")) {
        installExtensions(clojureExtensions)
    }
    if (options.contains("python")) {
        installExtensions(pythonExtensions)
    }

    provisionAdditionalTools()
}


private fun Prov.prerequisitesVSCinstall() = def {
    aptInstall("curl gpg unzip apt-transport-https")
}


@Suppress("unused") // only required for installation of vscode via apt
private fun Prov.installVscWithApt() = requireAll {
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


private fun Prov.installVSCPackage() = def {
    cmd("sudo snap install code --classic")

    // to install via apt use:
    //    installVscWithApt()
}


private fun Prov.installExtensions(extensions: List<String>) = optional {
    var res = ProvResult(true)
    for (ext in extensions) {
        res = cmd("code --install-extension $ext")
    }
    res
    // Settings can be found at $HOME/.config/Code/User/settings.json
}


private fun Prov.provisionAdditionalTools() = requireAll {
    // Joker
    cmd("curl -Lo joker-0.12.2-linux-amd64.zip https://github.com/candid82/joker/releases/download/v0.12.2/joker-0.12.2-linux-amd64.zip")
    cmd("unzip joker-0.12.2-linux-amd64.zip")
    cmd("sudo mv joker /usr/local/bin/")
}