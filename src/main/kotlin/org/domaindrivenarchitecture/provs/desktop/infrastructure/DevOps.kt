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
    installGraalVM()
}

fun Prov.installYq(
    version: String = "4.13.2",
    sha256sum: String = "d7c89543d1437bf80fee6237eadc608d1b121c21a7cbbe79057d5086d74f8d79"
) = task {
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

fun Prov.installKubectlAndTools() = task {

    task("installKubectl") {
        if (!checkFile(KUBE_CONFIG_CONTEXT_SCRIPT)) {
            installKubectl()
            configureKubectlBashCompletion()
        } else {
            ProvResult(true, out = "Kubectl already installed")
        }
    }

    task("installKubeconform") {

        installKubeconform()
    }
    installDevopsScripts()
}

fun Prov.installKubeconform() = task {
    // check for latest stable release on: https://github.com/yannh/kubeconform/releases
    val version = "0.6.4"
    val installationPath = "/usr/local/bin/"
    val tmpDir = "~/tmp"
    val filename = "kubeconform-linux-amd64"
    val packedFilename = "$filename.tar.gz"

    if ( !chk("kubeconform -v") || "v$version" != cmd("kubeconform -v").out?.trim() ) {
        downloadFromURL(
            "https://github.com/yannh/kubeconform/releases/download/v$version/$packedFilename",
            path = tmpDir,
            sha256sum = "2b4ebeaa4d5ac4843cf8f7b7e66a8874252b6b71bc7cbfc4ef1cbf85acec7c07"
        )
        cmd("sudo tar -xzf $packedFilename -C $installationPath", tmpDir)
    } else {
        ProvResult(true, out = "Kubeconform $version already installed")
    }
}

fun Prov.installKubectl() = task {

    // see https://kubernetes.io/docs/tasks/tools/install-kubectl-linux/
    val kubectlVersion = "1.27.4"
    val tmpDir = "~/tmp"

    // prerequisites -- see https://kubernetes.io/docs/tasks/tools/install-kubectl-linux/
    optional {
        cmd("sudo apt-get update")
    }
    aptInstall("apt-transport-https ca-certificates curl")
    createDir(tmpDir)
    downloadFromURL(
        "https://dl.k8s.io/release/v$kubectlVersion/bin/linux/amd64/kubectl",
        path = tmpDir,
        // from https://dl.k8s.io/v1.27.4/bin/linux/amd64/kubectl.sha256
        sha256sum = "4685bfcf732260f72fce58379e812e091557ef1dfc1bc8084226c7891dd6028f"
    )
    cmd("sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl", dir = tmpDir)
}

fun Prov.configureKubectlBashCompletion() = task {
    cmd("kubectl completion bash >> /etc/bash_completion.d/kubernetes", sudo = true)
    createDir(".bashrc.d")
    createFileFromResource(KUBE_CONFIG_CONTEXT_SCRIPT, "kubectl.sh", RESOURCE_PATH)
}

fun Prov.installDevopsScripts() = task {

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

fun Prov.installTerraform() = task {
    val dir = "/usr/lib/tfenv/"

    if (!checkDir(dir)) {
        createDirs(dir, sudo = true)
        cmd("git clone https://github.com/tfutils/tfenv.git " + dir, sudo = true)
        cmd("rm " + dir + ".git/ -rf", sudo = true)
        cmd("ln -s " + dir + "bin/* /usr/local/bin", sudo = true)
    }
    cmd("tfenv install", sudo = true)
    cmd("tfenv install latest:^1.4.6", sudo = true)
    cmd("tfenv use latest:^1.4.6", sudo = true)
}
