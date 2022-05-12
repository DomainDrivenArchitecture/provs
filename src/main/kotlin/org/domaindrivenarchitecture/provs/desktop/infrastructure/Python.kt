package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall


fun Prov.provisionPython() = task {
    installPython3()
    configureVenv()
    installPybuilder()
    installRestClient()
    installJupyterlab()
}

fun Prov.installPython3(): ProvResult = task {
    aptInstall("python3.8-venv python3-pip")
}

fun Prov.configureVenv(): ProvResult = task {
    val venvHome = "~/.venv/meissa"
    cmd("python3 -m venv " + venvHome)
    cmd("source " + venvHome + "/bin/activate")
    cmd("ln -s " + venvHome + "/bin/activate ~/.bashrc.d/venv.sh")
    cmd("pip3 install pip --upgrade")
}

fun Prov.installPybuilder(): ProvResult = task {
    cmd("pip3 install pybuilder ddadevops pypandoc mockito coverage unittest-xml-reporting deprecation python_terraform " +
            "boto3")
}

fun Prov.installRestClient(): ProvResult = task {
    cmd("pip3 install requests")
}

fun Prov.installJupyterlab(): ProvResult = task {
    cmd("pip3 install jupyterlab pandas matplotlib")
}
