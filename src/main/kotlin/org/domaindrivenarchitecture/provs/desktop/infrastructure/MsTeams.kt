package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall

fun Prov.installMsTeams() = task {
    aptInstall("curl")
    cmd("curl https://packages.microsoft.com/keys/microsoft.asc | sudo apt-key add -")
    cmd("sudo sh -c 'echo \"deb [arch=amd64] https://packages.microsoft.com/repos/ms-teams stable main\" > /etc/apt/sources.list.d/teams.list'")
    //cmd("sudo apt update")
    aptInstall("teams")
    //cmd("sudo apt install teams")
}