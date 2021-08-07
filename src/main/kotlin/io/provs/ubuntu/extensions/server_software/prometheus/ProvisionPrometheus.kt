package io.provs.ubuntu.extensions.server_software.prometheus

import io.provs.core.Prov
import io.provs.ubuntu.extensions.server_software.prometheus.base.*

/**
 * Provisions prometheus monitoring.
 * If running behind an nginx, pls specify the hostname in parameter nginxHost (e.g. mydomain.com).
 * To run it without nodeExporter (which provides system data to prometheus), set withNodeExporter to false.
 */
fun Prov.provisionPrometheusDocker(nginxHost: String? = null, withNodeExporter: Boolean = true) = def {
    configurePrometheusDocker()
    if (withNodeExporter) {
        installNodeExporter()
        runNodeExporter()
        addNodeExporterToPrometheusConf()
    }
    runPrometheusDocker(nginxHost)
}