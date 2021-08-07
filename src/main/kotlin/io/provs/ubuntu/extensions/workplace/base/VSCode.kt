package io.provs.ubuntu.extensions.workplace.base

import io.provs.Prov
import io.provs.ProvResult
import io.provs.ubuntu.install.base.aptInstall


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
private fun Prov.configurePackageManagerForVsc() = requireAll {
        // see https://code.visualstudio.com/docs/setup/linux
    // alternatively install with snapd (but this cannot be tested within docker as snapd within docker is not working/supported)

    sh(
        """
    curl https://packages.microsoft.com/keys/microsoft.asc | gpg --dearmor > packages.microsoft.gpg
    sudo install -o root -g root -m 644 packages.microsoft.gpg /etc/apt/trusted.gpg.d/
    sudo sh -c 'echo \"deb [arch=amd64 signed-by=/etc/apt/trusted.gpg.d/packages.microsoft.gpg] https://packages.microsoft.com/repos/vscode stable main\" > /etc/apt/sources.list.d/vscode.list'
    """
    )
    aptInstall("apt-transport-https")
    aptInstall("code")
}


private fun Prov.installVSCPackage() = def {
    cmd("sudo snap install code --classic")

    // to install via apt use:
    //    configurePackageManagerForVsc()
    //    aptInstall("code")

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
