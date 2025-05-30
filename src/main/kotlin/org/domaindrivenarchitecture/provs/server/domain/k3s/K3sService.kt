package org.domaindrivenarchitecture.provs.server.domain.k3s

import org.domaindrivenarchitecture.provs.configuration.infrastructure.DefaultConfigFileRepository
import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.scheduledjobs.domain.scheduleMonthlyReboot
import org.domaindrivenarchitecture.provs.server.domain.hetzner_csi.HetznerCSIConfigResolved
import org.domaindrivenarchitecture.provs.server.domain.hetzner_csi.provisionHetznerCSI
import org.domaindrivenarchitecture.provs.server.domain.k8s_grafana_agent.GrafanaAgentConfigResolved
import org.domaindrivenarchitecture.provs.server.domain.k8s_grafana_agent.provisionGrafanaAgent
import org.domaindrivenarchitecture.provs.server.infrastructure.DefaultApplicationFileRepository
import org.domaindrivenarchitecture.provs.server.infrastructure.deprovisionK3sInfra
import org.domaindrivenarchitecture.provs.server.infrastructure.findHetznerCSIConfig
import org.domaindrivenarchitecture.provs.server.infrastructure.findK8sGrafanaConfig
import org.domaindrivenarchitecture.provs.server.infrastructure.getK3sConfig
import org.domaindrivenarchitecture.provs.server.infrastructure.installK3s
import org.domaindrivenarchitecture.provs.server.infrastructure.installK9s
import org.domaindrivenarchitecture.provs.server.infrastructure.provisionK3sApplication
import org.domaindrivenarchitecture.provs.server.infrastructure.provisionK3sCertManager
import org.domaindrivenarchitecture.provs.server.infrastructure.provisionK3sEcho
import org.domaindrivenarchitecture.provs.server.infrastructure.provisionNetwork
import org.domaindrivenarchitecture.provs.server.infrastructure.provisionServerCliConvenience
import kotlin.system.exitProcess


fun Prov.provisionK3sCommand(cli: K3sCliCommand) = task {

    val grafanaConfigResolved: GrafanaAgentConfigResolved? = findK8sGrafanaConfig(cli.configFileName)?.resolveSecret()
    val hcloudConfigResolved: HetznerCSIConfigResolved? = findHetznerCSIConfig(cli.configFileName)?.resolveSecret()

    if (cli.onlyModules == null) {
        val k3sConfig: K3sConfig = getK3sConfig(cli.configFileName)
        DefaultConfigFileRepository().assertExists(cli.configFileName)
        val k3sConfigReprovision = k3sConfig.copy(reprovision = cli.reprovision || k3sConfig.reprovision)

        val applicationFiles = cli.applicationFileNames?.map { DefaultApplicationFileRepository(it).getFile() }
        provisionK3s(k3sConfigReprovision, grafanaConfigResolved, hcloudConfigResolved, applicationFiles)
    } else {
        cli.onlyModules.forEach { module ->
            when (module.uppercase()) {
                ServerOnlyModule.MONTHLY_REBOOT.name -> scheduleMonthlyReboot()
                ServerOnlyModule.HETZNER_CSI.name -> provisionHetznerCSI(hcloudConfigResolved)
                ServerOnlyModule.GRAFANA.name -> provisionGrafana(grafanaConfigResolved)
            }
        }
    }
}

/**
 * Installs a k3s server.
 */
fun Prov.provisionK3s(
    k3sConfig: K3sConfig,
    grafanaConfigResolved: GrafanaAgentConfigResolved? = null,
    hetznerCSIConfigResolved: HetznerCSIConfigResolved? = null,
    applicationFiles: List<ApplicationFile>? = null
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

    if (applicationFiles != null) {
        for (file in applicationFiles) {
            provisionK3sApplication(file)
        }
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
    grafanaConfigResolved: GrafanaAgentConfigResolved?
) = task {
    if (grafanaConfigResolved == null) {
        println("ERROR: Could not find grafana config.")
        exitProcess(7)
    }
    provisionGrafanaAgent(grafanaConfigResolved)
}

private fun Prov.provisionHetznerCSI(
    hetznerCSIConfigResolved: HetznerCSIConfigResolved?
) = task {
        if (hetznerCSIConfigResolved == null) {
            println("ERROR: Could not find hetznerCSI config.")
            exitProcess(7)
        }
        provisionHetznerCSI(hetznerCSIConfigResolved)
}

