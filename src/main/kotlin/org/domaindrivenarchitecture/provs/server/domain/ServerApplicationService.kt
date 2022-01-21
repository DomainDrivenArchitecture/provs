package org.domaindrivenarchitecture.provs.server.application

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.server.domain.installK3sServer


/**
 * Performs use case of provisioning a k3s server
 */
fun Prov.provisionK3s() = task {
    installK3sServer()
}

