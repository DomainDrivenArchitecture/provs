package org.domaindrivenarchitecture.provs.workplace.infrastructure

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.ProvResult
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.createDirs
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.dirExists
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.createFile


fun Prov.installDevOps() = def {
    installTerraform()
    installAwsCredentials("", "")                       // TODO: get credentials from gopass 
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