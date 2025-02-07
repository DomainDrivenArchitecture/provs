package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.*
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.checkCommand
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.checkPackage

fun Prov.installOpentofu(
    enforceVersion: Boolean = false
) = taskWithResult {

    if (checkCommand("tofu -version") && !enforceVersion) {
        val versionInst = cmd("tofu -version").toString()
        return@taskWithResult ProvResult(true, out = "Opentofu v$versionInst is already installed.")
    }

    val pathKeyrings = "/etc/apt/keyrings"

    val result = checkDir(pathKeyrings)
    if (result) {
        cmd("curl -fsSL https://get.opentofu.org/opentofu.gpg | sudo tee /etc/apt/keyrings/opentofu.gpg > /dev/null")
        cmd("curl -fsSL https://packages.opentofu.org/opentofu/tofu/gpgkey/ | sudo gpg --no-tty --batch --dearmor -o /etc/apt/keyrings/opentofu-repo.gpg > /dev/null")
        cmd("chmod a+r /etc/apt/keyrings/opentofu.gpg /etc/apt/keyrings/opentofu-repo.gpg", sudo = true)

        val TofuListFile = "/etc/apt/sources.list.d/opentofu.list"
        val content = """deb [signed-by=/etc/apt/keyrings/opentofu.gpg,/etc/apt/keyrings/opentofu-repo.gpg] https://packages.opentofu.org/opentofu/tofu/any/ any main""" + "\n" +
                """deb-src [signed-by=/etc/apt/keyrings/opentofu.gpg,/etc/apt/keyrings/opentofu-repo.gpg] https://packages.opentofu.org/opentofu/tofu/any/ any main""" + "\n" +
                "".trimIndent()
        createFile(TofuListFile, content, sudo = true)

        cmd("sudo apt-get update -q=2")
        aptInstall("tofu")
        addResult(checkPackage("tofu"), info = "Opentofu has been installed.")
    }else {
        return@taskWithResult ProvResult(false, err = "Opentofu could not be downloaded and installed. ")
    }
}
