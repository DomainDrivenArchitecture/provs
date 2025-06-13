package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.infrastructure.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.infrastructure.checkPackage


fun Prov.installVSCode(vararg options: String) = task {
    val clojureExtensions = setOf("betterthantomorrow.calva", "DavidAnson.vscode-markdownlint")
    val pythonExtensions = setOf("ms-python.python")

    installVSCodePrerequisites()

    installVSCPackage()
    installVSCodiumPackage()

    if (options.contains("clojure")) {
        installVSCodeExtensions(clojureExtensions)
        installVSCodiumExtensions(clojureExtensions)
    }
    if (options.contains("python")) {
        installVSCodeExtensions(pythonExtensions)
        installVSCodiumExtensions(pythonExtensions)
    }
}


private fun Prov.installVSCodePrerequisites() = task {
    aptInstall("curl gpg unzip apt-transport-https")
}


@Suppress("unused") // only required for installation of vscode via apt
private fun Prov.installVSCodeWithApt() = task {
    val packageName = "code"
    if (!checkPackage(packageName)) {
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


private fun Prov.installVSCodeExtensions(extensions: Set<String>) = optional {
    var res = ProvResult(true)
    for (ext in extensions) {
        res = cmd("code --install-extension $ext")
    }
    res
    // Settings can be found at $HOME/.config/Code/User/settings.json
}

private fun Prov.installVSCodiumExtensions(extensions: Set<String>) = optional {
    var res = ProvResult(true)
    for (ext in extensions) {
        res = ProvResult(res.success && cmd("codium --install-extension $ext").success)
    }
    res
    // Settings can be found at $HOME/.config/VSCodium/User/settings.json
}
