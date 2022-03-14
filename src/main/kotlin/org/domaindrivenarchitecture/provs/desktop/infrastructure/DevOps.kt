package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.core.local
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.*
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.base.downloadFromURL


fun Prov.installDevOps() = def {
    installTerraform()
    installKubectlAndTools()
    installYq()
    installAwsCredentials()
}


fun Prov.installYq(
    version: String = "4.13.2",
    sha256sum: String = "d7c89543d1437bf80fee6237eadc608d1b121c21a7cbbe79057d5086d74f8d79"
): ProvResult = def {
    val path = "/usr/bin/"
    val filename = "yq"
    if (!fileExists(path + filename)) {
        downloadFromURL(
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

fun Prov.installKubectlAndTools(): ProvResult = def {
    val resourcePath = "org/domaindrivenarchitecture/provs/desktop/infrastructure"

    task("installKubectl") {
        val kubeConfigFile = "~/.bashrc.d/kubectl.sh"
        if (!fileExists(kubeConfigFile)) {
            // prerequisites -- see https://kubernetes.io/docs/tasks/tools/install-kubectl-linux/
            cmd("sudo apt-get update")
            aptInstall("apt-transport-https ca-certificates curl")
            cmd("sudo curl -fsSLo /usr/share/keyrings/kubernetes-archive-keyring.gpg https://packages.cloud.google.com/apt/doc/apt-key.gpg")
            cmd("echo \"deb [signed-by=/usr/share/keyrings/kubernetes-archive-keyring.gpg] https://apt.kubernetes.io/ kubernetes-xenial main\" | sudo tee /etc/apt/sources.list.d/kubernetes.list")

            // kubectl and bash completion
            cmd("sudo apt-get update")
            aptInstall("kubectl")
            addTextToFile("\nkubectl completion bash\n", "/etc/bash_completion.d/kubernetes", sudo = true)
            createDir(".bashrc.d")
            createFileFromResource(kubeConfigFile, "kubectl.sh", resourcePath)
        } else {
            ProvResult(true, out = "Kubectl already installed")
        }
    }

    task("install ssh helper") {
        createFileFromResource(
            "/usr/local/bin/sshu.sh",
            "sshu.sh",
            resourcePath,
            "555",
            sudo = true
        )
        createFileFromResource(
            "/usr/local/bin/ssht.sh",
            "ssht.sh",
            resourcePath,
            "555",
            sudo = true
        )
    }

    task("install k8sCreateContext") {
        val k8sContextFile = "/usr/local/bin/k8s-create-context.sh"
        createFileFromResource(
            k8sContextFile,
            "k8s-create-context.sh",
            resourcePath,
            "555",
            sudo = true
        )

    }
}

fun Prov.installTerraform(): ProvResult = def {
    val dir = "/usr/lib/tfenv/"

    if (!dirExists(dir)) {
        createDirs(dir, sudo = true)
        cmd("git clone https://github.com/tfutils/tfenv.git " + dir, sudo = true)
        cmd("rm " + dir + ".git/ -rf", sudo = true)
        cmd("ln -s " + dir + "bin/* /usr/local/bin", sudo = true)
    }
    cmd("tfenv install", sudo = true)
    cmd("tfenv install latest:^1.0.8", sudo = true)
    cmd("tfenv use latest:^1.0.8", sudo = true)
}


// --------------------------------------------  AWS credentials file  -----------------------------------------------
fun Prov.installAwsCredentials(id: String = "REPLACE_WITH_YOUR_ID", key: String = "REPLACE_WITH_YOUR_KEY"): ProvResult =
    def {
        val dir = "~/.aws"

        if (!dirExists(dir)) {
            createDirs(dir)
            createFile("~/.aws/config", awsConfig())
            createFile("~/.aws/credentials", awsCredentials(id, key))
        } else {
            ProvResult(true, "aws credential folder already installed")
        }
    }

fun awsConfig(): String {
    return """
    [default]
    region = eu-central-1
    output = json
    """.trimIndent()
}

fun awsCredentials(id: String, key: String): String {
    return """
    [default]
    aws_access_key_id = $id
    aws_secret_access_key = $key
    """.trimIndent()
}

fun main() {
    local().installDevOps()
}