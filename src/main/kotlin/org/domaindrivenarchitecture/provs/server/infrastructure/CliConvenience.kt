package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult

fun Prov.provisionServerCliConvenience() = task {
    provisionKubectlCompletionAndAlias()
}

fun Prov.provisionKubectlCompletionAndAlias(): ProvResult = task {
    cmd("kubectl completion bash | sudo tee /etc/bash_completion.d/kubectl > /dev/null")
    cmd("echo 'alias k=kubectl' >>~/.bashrc")
    cmd("echo 'complete -o default -F __start_kubectl k' >>~/.bashrc")
}