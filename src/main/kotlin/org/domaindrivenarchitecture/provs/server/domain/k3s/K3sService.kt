package org.domaindrivenarchitecture.provs.server.domain.k3s

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.server.domain.k8s_grafana_agent.GrafanaAgentConfigResolved
import org.domaindrivenarchitecture.provs.server.domain.k8s_grafana_agent.provisionGrafanaAgent
import org.domaindrivenarchitecture.provs.server.infrastructure.*
import kotlin.system.exitProcess


fun Prov.provisionK3sCommand(cli: K3sCliCommand) = task {

    val grafanaConfigResolved: GrafanaAgentConfigResolved? = findK8sGrafanaConfig(cli.configFileName)?.resolveSecret()

    if (cli.submodules == null ) {
        val k3sConfig: K3sConfig = getK3sConfig(cli.configFileName)
        DefaultApplicationFileRepository().assertExists(cli.applicationFileName)

        if (cli.reprovision || k3sConfig.reprovision) {
            deprovisionK3sInfra()
        }
        provisionK3s(k3sConfig, grafanaConfigResolved, cli.applicationFileName)
    } else {
        provisionGrafana(cli.submodules, grafanaConfigResolved)
    }
}

/**
 * Installs a k3s server.
 */
fun Prov.provisionK3s(
    k3sConfig: K3sConfig,
    grafanaConfigResolved: GrafanaAgentConfigResolved? = null,
    applicationFileName: ApplicationFileName? = null) = task {

    provisionNetwork(k3sConfig)

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

    if (applicationFileName != null) {
        provisionK3sApplication(applicationFileName)
    }
    
    provisionServerCliConvenience()
}

private fun Prov.provisionGrafana(
    submodules: List<String>?,
    grafanaConfigResolved: GrafanaAgentConfigResolved?) = task {

    if (submodules != null && submodules.contains(ServerSubmodule.GRAFANA.name.lowercase())) {
        if (grafanaConfigResolved == null) {
            println("ERROR: Could not find grafana config.")
            exitProcess(7)
        }
        provisionGrafanaAgent(grafanaConfigResolved)
    }
}
