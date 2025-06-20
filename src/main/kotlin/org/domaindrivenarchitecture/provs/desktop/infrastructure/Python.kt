package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.createSymlink
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.infrastructure.aptInstall
import java.io.File


fun Prov.provisionPython(venvHome: String? = "~/.venv/meissa") = task {
    installPython3()
    if (venvHome != null) { configureVenv(venvHome) }
    installPybuilder(venvHome)
    installRestClient(venvHome)
    installJupyterlab(venvHome)
    installLinters(venvHome)
    installAsciinema(venvHome)
    installPyTest(venvHome)
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
                " python_terraform dda_python_terraform boto3 pyyaml packaging inflection",
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

fun Prov.installLinters(venvHome: String? = null): ProvResult = task {
    pipInstall("flake8 mypy pylint", venvHome)
}
fun Prov.installAsciinema(venvHome: String? = null): ProvResult = task {
    pipInstall("asciinema", venvHome)
}

fun Prov.installPyTest(venvHome: String? = null): ProvResult = task {
    pipInstall("pytest", venvHome)
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
