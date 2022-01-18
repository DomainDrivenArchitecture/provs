package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov


fun Prov.installIntelliJ() = task {
    cmd("sudo snap install intellij-idea-community --classic")
}
