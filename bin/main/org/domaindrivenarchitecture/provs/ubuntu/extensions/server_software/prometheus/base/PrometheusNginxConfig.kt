package org.domaindrivenarchitecture.provs.ubuntu.extensions.server_software.prometheus.base

val prometheusNginxConfig = """
                proxy_pass http://localhost:9090/prometheus;
"""