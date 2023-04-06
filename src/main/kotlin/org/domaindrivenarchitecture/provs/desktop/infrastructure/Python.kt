package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createSymlink
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import java.io.File


fun Prov.provisionPython() = task {
    installPython3()
    configureVenv()
    installPybuilder()
    installRestClient()
    installJupyterlab()
}

fun Prov.installPython3(): ProvResult = task {
    aptInstall("python3-venv python3-pip")
}

fun Prov.configureVenv(): ProvResult = task {
    val venvHome = "~/.venv/meissa"
    cmd("python3 -m venv " + venvHome)
    cmd("source " + venvHome + "/bin/activate")
    createSymlink(File(venvHome + "/bin/activate"), File("~/.bashrc.d/venv.sh"))
    cmd("pip3 install pip --upgrade")
}

fun Prov.installPybuilder(): ProvResult = task {
    cmd("pip3 install pybuilder ddadevops pypandoc mockito coverage unittest-xml-reporting deprecation" +
            " python_terraform dda_python_terraform boto3 pyyaml ")
    cmd("pip3 install --upgrade ddadevops")
}

fun Prov.installRestClient(): ProvResult = task {
    cmd("pip3 install requests")
}

fun Prov.installJupyterlab(): ProvResult = task {
    cmd("pip3 install jupyterlab pandas matplotlib")
}
