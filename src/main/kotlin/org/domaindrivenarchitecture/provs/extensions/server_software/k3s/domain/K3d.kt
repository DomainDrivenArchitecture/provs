package org.domaindrivenarchitecture.provs.extensions.server_software.k3s.domain

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.docker.provideContainer
import org.domaindrivenarchitecture.provs.core.echoCommandForTextWithNewlinesReplaced
import org.domaindrivenarchitecture.provs.core.repeatTask


/**
 * Runs a k3s server and a k3s agent as containers.
 */
fun Prov.installK3sAsContainers(token: String = "12345678901234") = task {
    cmd("docker volume create k3s-server")
    provideContainer("k3s-server", "rancher/k3s", command = "server --cluster-init", options = "-d --privileged --tmpfs /run --tmpfs /var/run -e K3S_TOKEN=$token -e K3S_KUBECONFIG_OUTPUT=./kubeconfig.yaml -e K3S_KUBECONFIG_MODE=666 -v k3s-server:/var/lib/rancher/k3s:z -p 6443:6443 -p 80:80 -p 443:443")
    provideContainer("k3s-agent", "rancher/k3s", options = "-d --privileged --tmpfs /run  --tmpfs /var/run -e K3S_TOKEN=$token -e K3S_URL=https://server:6443")

    // wait for config file
    cmd("export timeout=60; while [ ! -f /var/lib/docker/volumes/k3s-server/_data/server/kubeconfig.yaml ]; do if [ \"${'$'}timeout\" == 0 ]; then echo \"ERROR: Timeout while waiting for file.\"; break; fi; sleep 1; ((timeout--)); done")

    sh("""
        mkdir -p ${'$'}HOME/.kube/
        cp /var/lib/docker/volumes/k3s-server/_data/server/kubeconfig.yaml ${'$'}HOME/.kube/config        
    """.trimIndent())
}


/**
 * Apply a config to kubernetes.
 * Prerequisite: Kubectl has to be installed
 */
fun Prov.applyK8sConfig(configAsYaml: String, kubectlCommand: String = "kubectl") = task {
    repeatTask(6, 10) {
        cmd(echoCommandForTextWithNewlinesReplaced(configAsYaml) + " | $kubectlCommand apply -f -")
    }
}
