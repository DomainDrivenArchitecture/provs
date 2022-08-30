package org.domaindrivenarchitecture.provs.server.domain

import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.configuration.domain.TargetCliCommand

enum class ServerType {
    K3D, K3S
}

open class ServerCliCommand(
    val serverType: ServerType,
    val target: TargetCliCommand,
    val configFileName: ConfigFileName?,)
{
    fun isValidServerType(): Boolean {
        return serverType == ServerType.K3S
    }
    fun isValidTarget(): Boolean {
        return target.isValid()
    }
}
