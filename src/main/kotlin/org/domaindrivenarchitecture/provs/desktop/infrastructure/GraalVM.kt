package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.base.downloadFromURL

const val GRAAL_VM_VERSION = "21.0.2"


fun Prov.installGraalVM() = taskWithResult {
    val tmpDir = "~/tmp"
    val filename = "graalvm-community-jdk-"
    val additionalPartFilename = "_linux-x64_bin"
    val packedFilename = "$filename$GRAAL_VM_VERSION$additionalPartFilename.tar.gz"
    val extractedFilenameHunch = "graalvm-community-openjdk-"
    val installationPath = "/usr/lib/jvm/"

    if ( GRAAL_VM_VERSION != graalVMVersion() || !chk("ls -d $installationPath$extractedFilenameHunch$GRAAL_VM_VERSION*")) {
        downloadFromURL(
            "https://github.com/graalvm/graalvm-ce-builds/releases/download/jdk-$GRAAL_VM_VERSION/$packedFilename",
            path = tmpDir,
            sha256sum = "b048069aaa3a99b84f5b957b162cc181a32a4330cbc35402766363c5be76ae48"
        )
        if (!chk("ls -d $installationPath"))
            cmd("sudo mkdir $installationPath")
        else {
            ProvResult(true, out = "$installationPath just exists, mkdir not necessary.")
        }
        cmd("sudo tar -C $installationPath -xzf $packedFilename", tmpDir)
        val graalInstPath = installationPath + (cmd("ls /usr/lib/jvm/|grep -e graalvm-community-openjdk-$GRAAL_VM_VERSION").out?.replace("\n", ""))
        cmd("sudo ln -sf $graalInstPath/lib/svm/bin/native-image /usr/local/bin/native-image")
        cmd("/usr/local/bin/native-image --version")
    } else {
        ProvResult(true, out = "GraalVM $GRAAL_VM_VERSION already installed")
    }
}

fun Prov.graalVMVersion(): String {
    return cmdNoEval("/usr/local/bin/native-image --version|awk 'NR==1 {print $2}'").out?.trim() ?: ""
}