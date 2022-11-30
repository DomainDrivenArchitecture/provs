package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFileFromResource

private const val resourcePath = "org/domaindrivenarchitecture/provs/desktop/infrastructure"

fun Prov.provisionServerCliConvenience() = task {
    provisionKubectlCompletionAndAlias()
    provisionVimrc()
    provisionKubeEditor()
}

fun Prov.provisionKubectlCompletionAndAlias(): ProvResult = task {
    cmd("kubectl completion bash | sudo tee /etc/bash_completion.d/kubectl > /dev/null")
    cmd("echo 'alias k=kubectl' >> ~/.bashrc")
    cmd("echo 'complete -o default -F __start_kubectl k' >>~/.bashrc")
}

fun Prov.provisionVimrc(): ProvResult = task {
    createFileFromResource("~/.vimrc",".vimrc", resourcePath)
}

fun Prov.provisionKubeEditor(): ProvResult = task {
    cmd("echo '' >> ~/.profile")
    cmd("echo 'export KUBE_EDITOR=vim' >> ~/.profile")
}