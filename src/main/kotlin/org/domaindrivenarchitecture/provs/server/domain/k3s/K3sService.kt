package org.domaindrivenarchitecture.provs.server.domain.k3s

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.server.domain.k8s_grafana_agent.GrafanaAgentConfigResolved
import org.domaindrivenarchitecture.provs.server.domain.k8s_grafana_agent.provisionGrafanaAgent
import org.domaindrivenarchitecture.provs.server.infrastructure.*
import kotlin.system.exitProcess


fun Prov.provisionK3sCommand(cli: K3sCliCommand) = task {

    val grafanaConfigResolved: GrafanaAgentConfigResolved? = findK8sGrafanaConfig(cli.configFileName)?.resolveSecret()

    if (cli.onlyModules == null ) {
        val k3sConfig: K3sConfig = getK3sConfig(cli.configFileName)
        DefaultApplicationFileRepository().assertExists(cli.applicationFileName)

        val k3sConfigReprovision = k3sConfig.copy(reprovision = cli.reprovision || k3sConfig.reprovision)
        provisionK3s(k3sConfigReprovision, grafanaConfigResolved, cli.applicationFileName)
    } else {
        provisionGrafana(cli.onlyModules, grafanaConfigResolved)
    }
}

/**
 * Installs a k3s server.
 */
fun Prov.provisionK3s(
    k3sConfig: K3sConfig,
    grafanaConfigResolved: GrafanaAgentConfigResolved? = null,
    applicationFileName: ApplicationFileName? = null) = task {

    if (k3sConfig.reprovision) {
        deprovisionK3sInfra()
    }

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
    onlyModules: List<String>?,
    grafanaConfigResolved: GrafanaAgentConfigResolved?) = task {

    if (onlyModules != null && onlyModules.contains(ServerOnlyModule.GRAFANA.name.lowercase())) {
        if (grafanaConfigResolved == null) {
            println("ERROR: Could not find grafana config.")
            exitProcess(7)
        }
        provisionGrafanaAgent(grafanaConfigResolved)
    }
}
