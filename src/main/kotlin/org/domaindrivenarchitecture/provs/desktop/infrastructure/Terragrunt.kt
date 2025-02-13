package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.*
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.checkCommand
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.base.downloadFromURL

fun Prov.installTerragrunt(
    version: String = "0.72.6",
    reInstall: Boolean = false,
    sha256sum: String = "df63a41576b8b4129b498da5b698b5792a5a228ea5012bbecdcbe49d4d662be3"
) = taskWithResult {
    if (checkCommand("terragrunt") && !reInstall) {
        return@taskWithResult ProvResult(true)
    }

    if (checkTerragruntVersion(version)) {
        return@taskWithResult ProvResult(true, info = "Terragrunt is already installed.")
    }

    val downloadUrl = "https://github.com/gruntwork-io/terragrunt/releases/download/v$version/terragrunt_linux_amd64"
    val filename = "terragrunt_linux_amd64"
    val target = "${userHome()}tmp"
    val result = downloadFromURL(
        downloadUrl,
        filename,
        target,
        sha256sum = sha256sum
    )

    if (result.success) {
        cmd("sudo mv $target/$filename /usr/local/bin/terragrunt", sudo = true)
        cmd("chmod 755 /usr/local/bin/terragrunt", sudo = true)
        // check and assert installation
        addResult(checkTerragruntVersion(version), info = "Terragrunt version $version has been installed.")
    } else {
        return@taskWithResult ProvResult(false, err = "Terragrunt $version could not be downloaded and installed. " + result.err)
    }
}

fun Prov.checkTerragruntVersion(version: String): Boolean {
    val installedTerragruntVersion = terragruntVersion()
    return installedTerragruntVersion != null && installedTerragruntVersion.startsWith("terragrunt version v" + version)
}

internal fun Prov.terragruntVersion(): String? {
    val result = cmdNoEval("terragrunt --version", sudo = true)
    return if (!result.success) null else result.out
}