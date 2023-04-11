package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.*
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.base.downloadFromURL


private const val RESOURCE_PATH = "org/domaindrivenarchitecture/provs/desktop/infrastructure"
private const val KUBE_CONFIG_CONTEXT_SCRIPT = ".bashrc.d/kubectl.sh"


fun Prov.installDevOps() = task {
    installTerraform()
    installKubectlAndTools()
    installYq()
    installAwsCredentials()
    installDevOpsFolder()
}


fun Prov.installYq(
    version: String = "4.13.2",
    sha256sum: String = "d7c89543d1437bf80fee6237eadc608d1b121c21a7cbbe79057d5086d74f8d79"
): ProvResult = task {
    val path = "/usr/bin/"
    val filename = "yq"
    if (!checkFile(path + filename)) {
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

fun Prov.installKubectlAndTools(): ProvResult = task {

    task("installKubectl") {
        if (!checkFile(KUBE_CONFIG_CONTEXT_SCRIPT)) {
            installKubectl()
            configureKubectlBashCompletion()
        } else {
            ProvResult(true, out = "Kubectl already installed")
        }
    }

    installDevopsScripts()
}

fun Prov.installKubectl(): ProvResult = task {

    // see https://kubernetes.io/docs/tasks/tools/install-kubectl-linux/
    val kubectlVersion = "1.23.0"
    val tmpDir = "~/tmp"

    // prerequisites -- see https://kubernetes.io/docs/tasks/tools/install-kubectl-linux/
    cmd("sudo apt-get update")
    aptInstall("apt-transport-https ca-certificates curl")
    createDir(tmpDir)
    downloadFromURL(
        "https://dl.k8s.io/release/v$kubectlVersion/bin/linux/amd64/kubectl",
        path = tmpDir,
        // from https://dl.k8s.io/v1.23.0/bin/linux/amd64/kubectl.sha256
        sha256sum = "2d0f5ba6faa787878b642c151ccb2c3390ce4c1e6c8e2b59568b3869ba407c4f"
    )
    cmd("sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl", dir = tmpDir)
}

fun Prov.configureKubectlBashCompletion(): ProvResult = task {
    cmd("kubectl completion bash >> /etc/bash_completion.d/kubernetes", sudo = true)
    createDir(".bashrc.d")
    createFileFromResource(KUBE_CONFIG_CONTEXT_SCRIPT, "kubectl.sh", RESOURCE_PATH)
}

fun Prov.installDevopsScripts() {

    task("install ssh helper") {
        createFileFromResource(
            "/usr/local/bin/sshu.sh",
            "sshu.sh",
            RESOURCE_PATH,
            "555",
            sudo = true
        )
        createFileFromResource(
            "/usr/local/bin/ssht.sh",
            "ssht.sh",
            RESOURCE_PATH,
            "555",
            sudo = true
        )
    }

    task("install k3sCreateContext") {
        val k3sContextFile = "/usr/local/bin/k3s-create-context.sh"
        createFileFromResource(
            k3sContextFile,
            "k3s-create-context.sh",
            RESOURCE_PATH,
            "555",
            sudo = true
        )
    }

    task("install k3sConnect") {
        val k3sConnectFile = "/usr/local/bin/k3s-connect.sh"
        createFileFromResource(
            k3sConnectFile,
            "k3s-connect.sh",
            RESOURCE_PATH,
            "555",
            sudo = true
        )
    }
}

fun Prov.installTerraform(): ProvResult = task {
    val dir = "/usr/lib/tfenv/"

    if (!checkDir(dir)) {
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
    task {
        val dir = "~/.aws"

        if (!checkDir(dir)) {
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

fun Prov.installDevOpsFolder(): ProvResult = task {

    val dir = "~/.devops/"

    if (!checkDir(dir)) {
        createDirs(dir)
    }

}
