package org.domaindrivenarchitecture.provs.server.domain

import org.domaindrivenarchitecture.provs.framework.core.cli.TargetCliCommand

enum class ServerType {
    K3D, K3S
}

class ServerCliCommand(
    val serverType: ServerType,
    val target: TargetCliCommand,
    val configFileName: ConfigFileName?,)
{
    fun isValid(): Boolean {
        return target.isValid()
    }
}
