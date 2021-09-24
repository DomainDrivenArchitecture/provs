package org.domaindrivenarchitecture.provs.workplace.infrastructure

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.ProvResult
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.createDirs
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.dirExists
import org.domaindrivenarchitecture.provs.ubuntu.install.base.aptInstall

fun Prov.installPython() = def {
    installPython3()
    installVenv()
    installPybuilder()
    installRestClient()
    installJupyterlab()
}



fun Prov.installPython3(): ProvResult = def {
    aptInstall("python3.8-venv")
}

fun Prov.installVenv(): ProvResult = def {
    var venvHome = "~/.python/meissa"
    cmd("python3 -m venv " + venvHome)
    cmd("source " + venvHome + "/bin/activate")
    cmd("pip install pip --upgrade")
}

fun Prov.installPybuilder(): ProvResult = def {
    cmd("pip install pybuilder ddadevops pypandoc mockito coverage unittest-xml-reporting deprecation python_terraform " +
            "boto3")
}

fun Prov.installRestClient(): ProvResult = def {
    cmd("pip install requests")
}

fun Prov.installJupyterlab(): ProvResult = def {
    cmd("pip install jupyterlab pandas matplotlib")
}
