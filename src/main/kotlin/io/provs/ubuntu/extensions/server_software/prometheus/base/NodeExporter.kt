package io.provs.ubuntu.extensions.server_software.prometheus.base

import io.provs.Prov
import io.provs.local
import io.provs.ubuntu.filesystem.base.createDir
import io.provs.ubuntu.filesystem.base.createFile
import io.provs.ubuntu.filesystem.base.fileContainsText
import io.provs.ubuntu.filesystem.base.replaceTextInFile
import io.provs.ubuntu.install.base.aptInstall
import io.provs.ubuntu.user.base.whoami


internal val defaultInstallationDir = "/usr/local/bin/"


fun Prov.installNodeExporter() = requireAll {
    // inspired by https://devopscube.com/monitor-linux-servers-prometheus-node-exporter/ and
    // https://www.howtoforge.com/tutorial/how-to-install-prometheus-and-node-exporter-on-centos-8/#step-install-and-configure-nodeexporter
    val downloadFileBasename = "node_exporter-1.0.1.linux-amd64"
    val downloadFile = "$downloadFileBasename.tar.gz"
    val downloadPath = "~/tmp/"
    val fqFile = downloadPath + downloadFile

    aptInstall("curl")
    createDir("tmp")
    sh(
        """
        cd tmp && curl -LO https://github.com/prometheus/node_exporter/releases/download/v1.0.1/$downloadFile --output $downloadFile
        cd tmp && tar -xvf $fqFile -C $downloadPath

        sudo mv $downloadPath$downloadFileBasename/node_exporter $defaultInstallationDir"""
    )

}


fun Prov.runNodeExporter() = def {
    createFile("/etc/systemd/system/node_exporter.service", nodeExporterServiceConf(whoami()?:"nouserfound"), sudo = true)

    sh("""
    sudo systemctl daemon-reload

    # start the node_exporter service and enable it to launch everytime at system startup.
    sudo systemctl start node_exporter
    sudo systemctl enable node_exporter
    
    # check if running
    sudo systemctl status node_exporter --no-pager -l
    """)
}


fun Prov.addNodeExporterToPrometheusConf (
    prometheusConf: String = "/etc/prometheus/prometheus.yml",
    sudo: Boolean = true
) = requireAll {
    val prometheusConfNodeExporter = """
scrape_configs:
  - job_name: 'node_exporter'
    static_configs:
    - targets: ['172.17.0.1:9100']
"""
    if (!fileContainsText(prometheusConf, "- job_name: 'node_exporter'", sudo)) {
        replaceTextInFile(prometheusConf, "\nscrape_configs:\n", prometheusConfNodeExporter)
    }
    //    cmd("sudo systemctl restart prometheus")  for standalone
    cmd("sudo docker restart prometheus")
}


fun nodeExporterServiceConf(user: String, installationDir: String = defaultInstallationDir): String {
    return """
[Unit]
Description=Node Exporter
Wants=network-online.target
After=network-online.target

[Service]
User=$user
ExecStart=${installationDir}node_exporter

[Install]
WantedBy=default.target
"""
}
