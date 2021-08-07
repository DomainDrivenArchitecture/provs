package io.provs.ubuntu.extensions.server_compounds.monitoring

import io.provs.core.Prov
import io.provs.ubuntu.extensions.server_software.nginx.base.NginxConf
import io.provs.ubuntu.extensions.server_software.nginx.base.nginxHttpConf
import io.provs.ubuntu.extensions.server_software.nginx.provisionNginxStandAlone
import io.provs.ubuntu.extensions.server_software.prometheus.base.configurePrometheusDocker
import io.provs.ubuntu.extensions.server_software.prometheus.base.runPrometheusDocker


@Suppress("unused") // used externally
fun Prov.provisionMonitoring() = requireAll {
    configurePrometheusDocker()
    runPrometheusDocker()
    provisionNginxStandAlone(NginxConf.nginxHttpConf())
}


