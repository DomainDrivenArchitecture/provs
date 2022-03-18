package org.domaindrivenarchitecture.provs.framework.extensions.server_software.standalone_server.prometheus

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.extensions.server_software.standalone_server.prometheus.base.*

/**
 * Provisions prometheus monitoring.
 * If running behind an nginx, pls specify the hostname in parameter nginxHost (e.g. mydomain.com).
 * To run it without nodeExporter (which provides system data to prometheus), set withNodeExporter to false.
 */
@Suppress("unused")
fun Prov.provisionPrometheusDocker(nginxHost: String? = null, withNodeExporter: Boolean = true) = task {
    configurePrometheusDocker()
    if (withNodeExporter) {
        installNodeExporter()
        runNodeExporter()
        addNodeExporterToPrometheusConf()
    }
    runPrometheusDocker(nginxHost)
}