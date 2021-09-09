package org.domaindrivenarchitecture.provs.workplace.infrastructure

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.ProvResult
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.createDirs
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.dirExists

fun Prov.installDevOps() = def {
    installTerraform()
}

fun Prov.installTerraform(): ProvResult = def {
    val dir = "/usr/lib/tfenv/"

    if(!dirExists(dir)) {
        createDirs(dir, sudo = true)
        cmd("git clone https://github.com/tfutils/tfenv.git " + dir, sudo = true)
        cmd("rm " + dir + ".git/ -rf", sudo = true)
        cmd("ln -s " + dir + "bin/* /usr/local/bin", sudo = true)
    }
    cmd ("tfenv install", sudo = true)
    cmd ("tfenv install latest:^0.13", sudo = true)
    cmd ("tfenv use latest:^0.13", sudo = true)
}

