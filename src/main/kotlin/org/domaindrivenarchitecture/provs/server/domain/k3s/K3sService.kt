package org.domaindrivenarchitecture.provs.server.domain.k3s

import org.domaindrivenarchitecture.provs.configuration.infrastructure.DefaultConfigFileRepository
import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.cron.infrastructure.scheduleMonthlyReboot
import org.domaindrivenarchitecture.provs.server.domain.hetzner_csi.HetznerCSIConfigResolved
import org.domaindrivenarchitecture.provs.server.domain.hetzner_csi.provisionHetznerCSI
import org.domaindrivenarchitecture.provs.server.domain.k8s_grafana_agent.GrafanaAgentConfigResolved
import org.domaindrivenarchitecture.provs.server.domain.k8s_grafana_agent.provisionGrafanaAgent
import org.domaindrivenarchitecture.provs.server.infrastructure.*
import kotlin.system.exitProcess


fun Prov.provisionK3sCommand(cli: K3sCliCommand) = task {

    val grafanaConfigResolved: GrafanaAgentConfigResolved? = findK8sGrafanaConfig(cli.configFileName)?.resolveSecret()
    val hcloudConfigResolved: HetznerCSIConfigResolved? = findHetznerCSIConfig(cli.configFileName)?.resolveSecret()

    if (cli.onlyModules == null) {
        val k3sConfig: K3sConfig = getK3sConfig(cli.configFileName)
        DefaultConfigFileRepository().assertExists(cli.configFileName)
        val k3sConfigReprovision = k3sConfig.copy(reprovision = cli.reprovision || k3sConfig.reprovision)

        val applicationFile = cli.applicationFileName?.let { DefaultApplicationFileRepository(cli.applicationFileName).getFile() }
        provisionK3s(k3sConfigReprovision, grafanaConfigResolved, hcloudConfigResolved, applicationFile)
    } else {
        provisionGrafana(cli.onlyModules, grafanaConfigResolved)
        provisionHetznerCSI(cli.onlyModules, hcloudConfigResolved)
        scheduleMonthlyReboot(cli.onlyModules)
    }
}

/**
 * Installs a k3s server.
 */
fun Prov.provisionK3s(
    k3sConfig: K3sConfig,
    grafanaConfigResolved: GrafanaAgentConfigResolved? = null,
    hetznerCSIConfigResolved: HetznerCSIConfigResolved? = null,
    applicationFile: ApplicationFile? = null
) = task {

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

    if (hetznerCSIConfigResolved != null) {
        provisionHetznerCSI(hetznerCSIConfigResolved)
    }

    if (applicationFile != null) {
        provisionK3sApplication(applicationFile)
    }

    if (!k3sConfig.reprovision) {
        provisionServerCliConvenience()
    }

    if (k3sConfig.monthlyReboot) {
        scheduleMonthlyReboot()
    }

    installK9s()
}

private fun Prov.provisionGrafana(
    onlyModules: List<String>?,
    grafanaConfigResolved: GrafanaAgentConfigResolved?
) = task {

    if (onlyModules != null && onlyModules.contains(ServerOnlyModule.GRAFANA.name.lowercase())) {
        if (grafanaConfigResolved == null) {
            println("ERROR: Could not find grafana config.")
            exitProcess(7)
        }
        provisionGrafanaAgent(grafanaConfigResolved)
    }
}

private fun Prov.provisionHetznerCSI(
    onlyModules: List<String>?,
    hetznerCSIConfigResolved: HetznerCSIConfigResolved?
) = task {

    if (onlyModules != null && onlyModules.contains(ServerOnlyModule.HETZNER_CSI.name.lowercase())) {
        if (hetznerCSIConfigResolved == null) {
            println("ERROR: Could not find hetznerCSI config.")
            exitProcess(7)
        }
        provisionHetznerCSI(hetznerCSIConfigResolved)
    }

}

fun Prov.scheduleMonthlyReboot(
    onlyModules: List<String>?,
) = task {
    if (onlyModules != null && onlyModules.contains(ServerOnlyModule.MONTHLY_REBOOT.name.lowercase())) {
        scheduleMonthlyReboot()
    }
}
