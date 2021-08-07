package org.domaindrivenarchitecture.provs.ubuntu.extensions.server_software.prometheus.base

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.docker.containerRuns
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.createDirs
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.createFile
import org.domaindrivenarchitecture.provs.ubuntu.install.base.aptInstall


internal val configDir = "/etc/prometheus/"
internal val configFile = "prometheus.yml"


fun Prov.configurePrometheusDocker(config: String = prometheusDefaultConfig) = requireAll {
    createDirs(configDir, sudo = true)
    createFile(configDir + configFile, config, sudo = true)
}


fun Prov.runPrometheusDocker(nginxHost: String? = null) = requireAll {
    aptInstall("docker.io")

    val containerName = "prometheus"

    if (containerRuns(containerName)) {
        cmd("sudo docker restart $containerName")
    } else {
        if (nginxHost == null) {
            cmd(
                "sudo docker run -d -p 9090:9090 " +
                        " --name $containerName " +
                        " --restart on-failure:1" +
                        " -v prometheus-data:/prometheus" +
                        " -v $configDir$configFile:/etc/prometheus/prometheus.yml " +
                        " prom/prometheus"
            )
        } else {
            cmd(
                "sudo docker run -d -p 9090:9090 " +
                        " --name $containerName " +
                        " --restart on-failure:1" +
                        " -v prometheus-data:/prometheus" +
                        " -v $configDir$configFile:/etc/prometheus/prometheus.yml " +
                        " prom/prometheus --config.file=/etc/prometheus/prometheus.yml --storage.tsdb.path=/prometheus " +
                        "--web.console.libraries=/usr/share/prometheus/console_libraries " +
                        "--web.console.templates=/usr/share/prometheus/consoles " +
                        "--web.external-url=http://$nginxHost/prometheus"
            )
        }
    }
}


private const val prometheusDefaultConfig =
    """
global:
  scrape_interval:     15s # By default, scrape targets every 15 seconds.

  # Attach these labels to any time series or alerts when communicating with
  # external systems (federation, remote storage, Alertmanager).
  external_labels:
    monitor: 'codelab-monitor'

# A scrape configuration containing exactly one endpoint to scrape:
# Here it's Prometheus itself.
scrape_configs:
  # The job name is added as a label `job=<job_name>` to any timeseries scraped from this config.
  - job_name: 'prometheus'

    # Override the global default and scrape targets from this job every 5 seconds.
    scrape_interval: 5s

    static_configs:
      - targets: ['localhost:9090']
"""
