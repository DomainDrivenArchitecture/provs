package org.domaindrivenarchitecture.provs.server.domain.k3s

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.server.infrastructure.*
import org.domaindrivenarchitecture.provs.server.infrastructure.k3s.getK3sConfig

/**
 * Installs a k3s server.
 */
fun Prov.provisionK3s(configFileName: ConfigFileName?) = task {
    val k3sConfig: K3sConfig = getK3sConfig(configFileName)

    provisionNetwork(k3sConfig)
    if (k3sConfig.reprovision && testConfigExists()) {
        deprovisionK3sInfra()
    }

    provisionK3sInfra(k3sConfig)

    if (k3sConfig.certmanager != null) {
        provisionK3sCertManager(k3sConfig.certmanager)
    }
    if (k3sConfig.echo == true) {
        provisionK3sEcho(k3sConfig.fqdn, k3sConfig.certmanager?.letsencryptEndpoint)
    }
    ProvResult(true)
}
