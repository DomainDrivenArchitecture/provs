package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFileFromResource
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFileFromResourceTemplate
import org.domaindrivenarchitecture.provs.server.domain.k3s.FileMode
import java.io.File


private const val grafanaResourceDir = "org/domaindrivenarchitecture/provs/server/infrastructure/grafana/"


fun Prov.provisionGrafanaAgentForK8s(user: String, password: Secret, clusterName: String, url: String) = task {
    val namespace = "monitoring"

    // Create namespace if not yet existing
    if (!chk("kubectl get namespace $namespace")) {
        cmd("kubectl create namespace $namespace")
    }

    // Deploy grafana-agent
    applyGrafanaFileFromResource(File(k3sManualManifestsDir, "grafana-agent.yaml"))

    // Deploy node-exporter
    applyGrafanaFileFromResource(File(k3sManualManifestsDir, "node-exporter-daemon-set.yaml"))

    // Deploy grafana config
    createFileFromResourceTemplate(
        k3sManualManifestsDir + "grafana-agent-config-map.yaml",
        "grafana-agent-config-map.template.yaml",
        resourcePath = grafanaResourceDir,
        posixFilePermission = "644",
        values = mapOf(
            "USERNAME" to user,
            "APIKEY" to password.plain(),
            "CLUSTERNAME" to clusterName,
            "URL" to url,
        )
    )
    cmd("export NAMESPACE=$namespace && kubectl apply -n \$NAMESPACE -f grafana-agent-config-map.yaml", k3sManualManifestsDir)

    // restart grafana-agent
    cmd("kubectl -n $namespace rollout restart deployment/grafana-agent")
}

// ============================  private functions  =============================

private fun Prov.createGrafanaFileFromResource(
    file: File,
    posixFilePermission: FileMode? = "644"
) = task {
    createFileFromResource(
        file.path,
        file.name,
        grafanaResourceDir,
        posixFilePermission,
        sudo = true
    )
}

private fun Prov.applyGrafanaFileFromResource(file: File, posixFilePermission: String? = "644") = task {
    createGrafanaFileFromResource(file, posixFilePermission)
    cmd("kubectl apply -f ${file.path}", sudo = true)
}
