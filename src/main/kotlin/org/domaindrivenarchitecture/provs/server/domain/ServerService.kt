package org.domaindrivenarchitecture.provs.server.domain

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.server.domain.k3s.provisionK3s

fun provisionServer(prov: Prov,  cmd: ServerCliCommand) {
    when(cmd.serverType) {
        ServerType.K3S -> prov.provisionK3s(cmd.configFileName)
    }
}