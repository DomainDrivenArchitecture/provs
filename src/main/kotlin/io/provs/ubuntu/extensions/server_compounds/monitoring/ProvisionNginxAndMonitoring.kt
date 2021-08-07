package io.provs.ubuntu.extensions.server_compounds.monitoring

import io.provs.core.Prov
import io.provs.ubuntu.extensions.server_software.nginx.base.NginxConf
import io.provs.ubuntu.extensions.server_software.nginx.base.nginxAddLocation
import io.provs.ubuntu.extensions.server_software.nginx.base.nginxCreateSelfSignedCertificate
import io.provs.ubuntu.extensions.server_software.nginx.base.nginxHttpsConfWithLocationFiles
import io.provs.ubuntu.extensions.server_software.nginx.provisionNginxStandAlone
import io.provs.ubuntu.extensions.server_software.prometheus.base.prometheusNginxConfig
import io.provs.ubuntu.extensions.server_software.prometheus.provisionPrometheusDocker


@Suppress("unused") // used externally
fun Prov.provisionNginxMonitoring(nginxHost: String = "localhost") = def {
    provisionPrometheusDocker(nginxHost)
    nginxCreateSelfSignedCertificate()
    provisionNginxStandAlone(NginxConf.nginxHttpsConfWithLocationFiles())
    nginxAddLocation("443", nginxHost, "/prometheus", prometheusNginxConfig)
}

