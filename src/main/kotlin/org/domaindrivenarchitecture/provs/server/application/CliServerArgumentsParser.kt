package org.domaindrivenarchitecture.provs.server.application

import kotlinx.cli.ArgType
import kotlinx.cli.default
import org.domaindrivenarchitecture.provs.framework.core.cli.CliTargetParser

class CliServerArgumentsParser(name: String) : CliTargetParser(name) {

    enum class K3sType {
        K3S, K3D
    }

    val type by option(
        ArgType.String,
        "type",
        "t",
        "either k3s (for standalone) or k3d for k3s running in a container"
    ).default("k3s")
}