package org.domaindrivenarchitecture.provs.server.domain.k3s

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.server.domain.k8s_grafana_agent.GrafanaAgentConfigResolved
import org.domaindrivenarchitecture.provs.server.domain.k8s_grafana_agent.provisionGrafanaAgent
import org.domaindrivenarchitecture.provs.server.infrastructure.*

/**
 * Installs a k3s server.
 */
fun Prov.provisionK3s(cli: K3sCliCommand) = task {
    val k3sConfig: K3sConfig = getK3sConfig(cli.configFileName)
    val grafanaConfigResolved: GrafanaAgentConfigResolved? = findK8sGrafanaConfig(cli.configFileName)?.resolveSecret()

    provisionNetwork(k3sConfig)
    if (k3sConfig.reprovision && testConfigExists()) {
        deprovisionK3sInfra()
    }

    installK3s(k3sConfig)

    if (k3sConfig.certmanager != null) {
        provisionK3sCertManager(k3sConfig.certmanager)
    }

    if (k3sConfig.echo == true) {
        provisionK3sEcho(k3sConfig.fqdn, k3sConfig.certmanager?.letsencryptEndpoint)
    }

    if (grafanaConfigResolved != null) {
        provisionGrafanaAgent(grafanaConfigResolved)
    }

    if (cli.applicationFileName != null) {
        provisionK3sApplication(cli.applicationFileName)
    }
}
