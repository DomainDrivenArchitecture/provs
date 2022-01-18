package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall


fun Prov.installPython() = def {
    installPython3()
    installVenv()
    installPybuilder()
    installRestClient()
    installJupyterlab()
}

fun Prov.installPython3(): ProvResult = def {
    aptInstall("python3.8-venv python3-pip")
}

fun Prov.installVenv(): ProvResult = def {
    val venvHome = "~/.python/meissa"
    cmd("python3 -m venv " + venvHome)
    cmd("source " + venvHome + "/bin/activate")
    cmd("pip3 install pip --upgrade")
}

fun Prov.installPybuilder(): ProvResult = def {
    cmd("pip3 install pybuilder ddadevops pypandoc mockito coverage unittest-xml-reporting deprecation python_terraform " +
            "boto3")
}

fun Prov.installRestClient(): ProvResult = def {
    cmd("pip3 install requests")
}

fun Prov.installJupyterlab(): ProvResult = def {
    cmd("pip3 install jupyterlab pandas matplotlib")
}
