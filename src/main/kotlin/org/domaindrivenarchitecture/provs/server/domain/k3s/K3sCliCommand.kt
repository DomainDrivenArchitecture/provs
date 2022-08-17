package org.domaindrivenarchitecture.provs.server.domain.k3s

import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.configuration.domain.TargetCliCommand
import org.domaindrivenarchitecture.provs.server.domain.ServerCliCommand
import org.domaindrivenarchitecture.provs.server.domain.ServerType
import org.domaindrivenarchitecture.provs.server.infrastructure.genericFileExistenceCheck

class K3sCliCommand(
    serverType: ServerType,
    target: TargetCliCommand,
    configFileName: ConfigFileName?,
    val applicationFileName: ApplicationFileName?,
    val submodules: List<String>? = null,
    val reprovision: Reprovision = false
) : ServerCliCommand(
    serverType,
    target,
    configFileName
) {
    fun isValidApplicationFileName(): Boolean {
        if (applicationFileName == null) {
            return true
        }
        return genericFileExistenceCheck(applicationFileName.fileName)
    }
}