package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createSymlink
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import java.io.File


fun Prov.provisionPython(venvHome: String? = "~/.venv/meissa") = task {
    installPython3()
    if (venvHome != null) { configureVenv(venvHome) }
    installPybuilder(venvHome)
    installRestClient(venvHome)
    installJupyterlab(venvHome)
}

fun Prov.installPython3(): ProvResult = task {
    aptInstall("python3-venv python3-pip")
}

fun Prov.configureVenv(venvHome: String): ProvResult = task {
    cmd("python3 -m venv $venvHome")
    createSymlink(File("$venvHome/bin/activate"), File("~/.bashrc.d/venv.sh"))
    pipInstall("pip --upgrade", venvHome)
}

fun Prov.installPybuilder(venvHome: String? = null): ProvResult = task {
    pipInstall("pybuilder ddadevops pypandoc mockito coverage unittest-xml-reporting deprecation" +
                " python_terraform dda_python_terraform boto3 pyyaml mfa packaging",
        venvHome
    )
    pipInstall("--upgrade ddadevops", venvHome)
}

fun Prov.installRestClient(venvHome: String? = null): ProvResult = task {
    pipInstall("requests", venvHome)
}

fun Prov.installJupyterlab(venvHome: String? = null): ProvResult = task {
    pipInstall("jupyterlab pandas matplotlib", venvHome)
}


private fun Prov.pipInstall(pkg: String, venvHome: String? = null) {
    cmd(activateVenvCommandPrefix(venvHome) + "pip3 install $pkg")
}

private fun activateVenvCommandPrefix(venvHome: String?): String {
    return if (venvHome == null) {
        ""
    } else {
        "source $venvHome/bin/activate && "
    }
}
