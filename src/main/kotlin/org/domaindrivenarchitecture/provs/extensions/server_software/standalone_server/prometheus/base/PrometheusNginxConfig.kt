package org.domaindrivenarchitecture.provs.extensions.server_software.standalone_server.prometheus.base

val prometheusNginxConfig = """
                proxy_pass http://localhost:9090/prometheus;
"""