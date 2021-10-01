package org.domaindrivenarchitecture.provs.workplace.infrastructure

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.ProvResult
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.createDirs
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.createFile
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.dirExists
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.fileExists
import org.domaindrivenarchitecture.provs.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.ubuntu.web.base.downloadFromURL


fun Prov.installDevOps() = def {
    installTerraform()
    //installAwsCredentials("", "")                       // TODO: get credentials from gopass
    installKubectl()
    installYq()
}


fun Prov.installYq(
    version: String = "4.13.2",
    sha256sum: String = "d7c89543d1437bf80fee6237eadc608d1b121c21a7cbbe79057d5086d74f8d79"
): ProvResult = def {
    var path = "/usr/bin/"
    var filename = "yq"
    if(!fileExists(path + filename)) {
        val result = downloadFromURL(
            "https://github.com/mikefarah/yq/releases/download/v$version/yq_linux_amd64",
            filename,
            path,
            sha256sum = sha256sum,
            sudo = true
        )
        cmd("chmod +x " + path + filename, sudo = true)
    } else {
        ProvResult(true)
    }
}

fun Prov.installKubectl(): ProvResult = def {
    var kubeConfigFile = "~/.bashrc.d/kubectl.sh"
    if(!fileExists(kubeConfigFile)) {
        aptInstall("kubectl")
        cmd("kubectl completion bash >> /etc/bash_completion.d/kubernetes", sudo = true)
        // TODO: externalize to file - trippeld escaping is realy ugly & does not work
        var kubeConfig = """
        # Set the default kube context if present
        DEFAULT_KUBE_CONTEXTS="$HOME/.kube/config"
        if test -f "${DEFAULT_KUBE_CONTEXTS}"
        then
          export KUBECONFIG="$DEFAULT_KUBE_CONTEXTS"
        fi
        
        # Additional contexts should be in ~/.kube/custom-contexts/ 
        CUSTOM_KUBE_CONTEXTS="$HOME/.kube/custom-contexts"
        mkdir -p "${CUSTOM_KUBE_CONTEXTS}"
        
        OIFS="$IFS"
        IFS=${'$'}'\n'
        for contextFile in `find "${CUSTOM_KUBE_CONTEXTS}" -type f -name "*.yml"`  
        do
            export KUBECONFIG="$contextFile:$KUBECONFIG"
        done
        IFS="$OIFS"

        """.trimIndent()
        createFile(kubeConfigFile, kubeConfig, "640")
    }

    val tunnelAliasFile = "~/.bashrc.d/ssh_alias.sh"
    if(!fileExists(tunnelAliasFile)) {
        var tunnelAlias = """
        alias sshu='ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no'
        alias ssht='ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -L 8002:localhost:8002 -L 6443:192.168.5.1:6443'    
        """.trimIndent()
        createFile(tunnelAliasFile, tunnelAlias, "640")
    }

    val k8sContextFile = "/usr/local/bin/k8s-create-context.sh"
    if(!fileExists(k8sContextFile)) {
        // TODO: externalize to file - trippeld escaping is realy ugly & does not work
        var k8sContext = """
        function main() {
            local cluster_name="${1}"; shift

            ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@\$\{cluster_name}.meissa-gmbh.de \
            "cat /etc/kubernetes/admin.conf" | \
            yq e ".clusters[0].name=\"\$\{cluster_name}\" \
                | .clusters[0].cluster.server=\"https://kubernetes:6443\" \
                | .contexts[0].context.cluster=\"\$\{cluster_name}\" \
                | .contexts[0].context.user=\"\$\{cluster_name}\" \
                | .contexts[0].name=\"\$\{cluster_name}\" \
                | del(.current-context) \
                | del(.preferences) \
                | .users[0].name=\"\$\{cluster_name}\"" - \
            > ~/.kube/custom-contexts/\$\{cluster_name}.yml
        }

        main $1
        
        """.trimIndent()
        createFile(k8sContextFile, k8sContext, "555", sudo = true)
    } else {
        ProvResult(true)
    }
}

fun Prov.installTerraform(): ProvResult = def {
    val dir = "/usr/lib/tfenv/"

    if(!dirExists(dir)) {
        createDirs(dir, sudo = true)
        cmd("git clone https://github.com/tfutils/tfenv.git " + dir, sudo = true)
        cmd("rm " + dir + ".git/ -rf", sudo = true)
        cmd("ln -s " + dir + "bin/* /usr/local/bin", sudo = true)
    }
    cmd ("tfenv install", sudo = true)
    cmd ("tfenv install latest:^0.13", sudo = true)
    cmd ("tfenv use latest:^0.13", sudo = true)
}

fun Prov.installAwsCredentials(id:String, key:String): ProvResult = def {
    val dir = "~/.aws"

    if(!dirExists(dir)) {
        createDirs(dir)
        createFile("~/.aws/config", awsConfig())
        createFile("~/.aws/credentials", awsCredentials(id, key))
    } else {
        ProvResult(true, "aws credential file already installed")
    }
}

fun awsConfig(): String {
    return """
    [default]
    region = eu-central-1
    output = json
    """.trimIndent()
}

fun awsCredentials(id:String, key:String): String {
    return """
    [default]
    aws_access_key_id = $id
    aws_secret_access_key = $key
    """.trimIndent()
}
