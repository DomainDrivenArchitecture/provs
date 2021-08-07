package org.domaindrivenarchitecture.provs.extensions.server_compounds.monitoring

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.extensions.server_software.nginx.base.NginxConf
import org.domaindrivenarchitecture.provs.extensions.server_software.nginx.base.nginxHttpConf
import org.domaindrivenarchitecture.provs.extensions.server_software.nginx.provisionNginxStandAlone
import org.domaindrivenarchitecture.provs.extensions.server_software.prometheus.base.configurePrometheusDocker
import org.domaindrivenarchitecture.provs.extensions.server_software.prometheus.base.runPrometheusDocker


@Suppress("unused") // used externally
fun Prov.provisionMonitoring() = requireAll {
    configurePrometheusDocker()
    runPrometheusDocker()
    provisionNginxStandAlone(NginxConf.nginxHttpConf())
}


