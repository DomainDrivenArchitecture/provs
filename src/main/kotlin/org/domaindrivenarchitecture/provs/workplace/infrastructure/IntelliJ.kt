package org.domaindrivenarchitecture.provs.workplace.infrastructure

import org.domaindrivenarchitecture.provs.core.Prov


fun Prov.installIntelliJ() = task {
    cmd("sudo snap install intellij-idea-community --classic")
}
