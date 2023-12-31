package org.domaindrivenarchitecture.provs.framework.ubuntu.git

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall


fun Prov.provisionGit(
    userName: String? = null,
    email: String? = null,
    signingKey: String? = null,
    diffTool: String? = null
): ProvResult = task {

    aptInstall("git")

    cmd("git config --global push.default simple")
    userName?.let { cmd("git config --global user.name $it") }
    email?.let { cmd("git config --global user.email $it") }
    signingKey?.let { cmd("git config --global user.signingkey $it") }
    diffTool?.let { cmd("git config --global --add diff.tool $it") } ?: ProvResult(true)
}