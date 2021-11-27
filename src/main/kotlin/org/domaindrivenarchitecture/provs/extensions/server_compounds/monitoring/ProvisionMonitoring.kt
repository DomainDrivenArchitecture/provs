package org.domaindrivenarchitecture.provs.extensions.server_compounds.monitoring

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.extensions.server_software.standalone_server.nginx.base.NginxConf
import org.domaindrivenarchitecture.provs.extensions.server_software.standalone_server.nginx.base.nginxHttpConf
import org.domaindrivenarchitecture.provs.extensions.server_software.standalone_server.nginx.provisionNginxStandAlone
import org.domaindrivenarchitecture.provs.extensions.server_software.standalone_server.prometheus.base.configurePrometheusDocker
import org.domaindrivenarchitecture.provs.extensions.server_software.standalone_server.prometheus.base.runPrometheusDocker


@Suppress("unused") // used externally
fun Prov.provisionMonitoring() = requireAll {
    configurePrometheusDocker()
    runPrometheusDocker()
    provisionNginxStandAlone(NginxConf.nginxHttpConf())
}


