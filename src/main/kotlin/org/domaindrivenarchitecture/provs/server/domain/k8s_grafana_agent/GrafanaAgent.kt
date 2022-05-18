package org.domaindrivenarchitecture.provs.server.domain.k8s_grafana_agent

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.server.infrastructure.provisionGrafanaAgentForK8s


fun Prov.provisionGrafanaAgent(configResolved: GrafanaAgentConfigResolved) =
    provisionGrafanaAgentForK8s(configResolved.user, configResolved.password, configResolved.cluster, configResolved.url)

