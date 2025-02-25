package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.*
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.checkCommand
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.checkPackage

fun Prov.installOpentofu(
    reInstall: Boolean = false
) = taskWithResult {

    if (checkCommand("tofu -version") && !reInstall) {
        val versionInst = cmd("tofu -version").out
        return@taskWithResult ProvResult(true, info = "Opentofu v$versionInst is already installed.")
    }

    val result = checkDir("/etc/apt/keyrings")
    if (result) {
        cmd("curl -fsSL https://get.opentofu.org/opentofu.gpg | sudo tee /etc/apt/keyrings/opentofu.gpg > /dev/null")
        cmd("curl -fsSL https://packages.opentofu.org/opentofu/tofu/gpgkey/ | sudo gpg --no-tty --batch --dearmor -o /etc/apt/keyrings/opentofu-repo.gpg > /dev/null")
        cmd("chmod a+r /etc/apt/keyrings/opentofu.gpg /etc/apt/keyrings/opentofu-repo.gpg", sudo = true)

        val tofuListFile = "/etc/apt/sources.list.d/opentofu.list"
        val content = """
            deb [signed-by=/etc/apt/keyrings/opentofu.gpg,/etc/apt/keyrings/opentofu-repo.gpg] https://packages.opentofu.org/opentofu/tofu/any/ any main
            deb-src [signed-by=/etc/apt/keyrings/opentofu.gpg,/etc/apt/keyrings/opentofu-repo.gpg] https://packages.opentofu.org/opentofu/tofu/any/ any main
            """.trimIndent() + "\n"
        createFile(tofuListFile, content, sudo = true)

        cmd("sudo apt-get update -q=2")
        aptInstall("tofu")
        if (checkPackage("tofu")) {
            ProvResult(true, info = "Opentofu is installed.")
        } else {
            ProvResult(false, err = "Opentofu not installed successfully.")
        }
    } else {
        ProvResult(false, err = "Opentofu could not be downloaded and installed.")
    }
}
