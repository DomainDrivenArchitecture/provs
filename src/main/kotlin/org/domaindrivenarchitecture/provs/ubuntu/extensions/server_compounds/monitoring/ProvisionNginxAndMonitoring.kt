package org.domaindrivenarchitecture.provs.ubuntu.extensions.server_compounds.monitoring

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.ubuntu.extensions.server_software.nginx.base.NginxConf
import org.domaindrivenarchitecture.provs.ubuntu.extensions.server_software.nginx.base.nginxAddLocation
import org.domaindrivenarchitecture.provs.ubuntu.extensions.server_software.nginx.base.nginxCreateSelfSignedCertificate
import org.domaindrivenarchitecture.provs.ubuntu.extensions.server_software.nginx.base.nginxHttpsConfWithLocationFiles
import org.domaindrivenarchitecture.provs.ubuntu.extensions.server_software.nginx.provisionNginxStandAlone
import org.domaindrivenarchitecture.provs.ubuntu.extensions.server_software.prometheus.base.prometheusNginxConfig
import org.domaindrivenarchitecture.provs.ubuntu.extensions.server_software.prometheus.provisionPrometheusDocker


@Suppress("unused") // used externally
fun Prov.provisionNginxMonitoring(nginxHost: String = "localhost") = def {
    provisionPrometheusDocker(nginxHost)
    nginxCreateSelfSignedCertificate()
    provisionNginxStandAlone(NginxConf.nginxHttpsConfWithLocationFiles())
    nginxAddLocation("443", nginxHost, "/prometheus", prometheusNginxConfig)
}

