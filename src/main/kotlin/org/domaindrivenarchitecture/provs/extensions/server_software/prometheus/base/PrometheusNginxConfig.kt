package org.domaindrivenarchitecture.provs.extensions.server_software.prometheus.base

val prometheusNginxConfig = """
                proxy_pass http://localhost:9090/prometheus;
"""