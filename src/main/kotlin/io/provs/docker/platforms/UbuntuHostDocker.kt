package io.provs.docker.platforms

import io.provs.*
import io.provs.docker.containerRuns
import io.provs.docker.dockerImageExists
import io.provs.docker.exitAndRmContainer
import io.provs.docker.images.DockerImage
import io.provs.platforms.UbuntuProv
import io.provs.processors.ContainerStartMode


fun UbuntuProv.provideContainerPlatform(
    containerName: String,
    imageName: String = "ubuntu",
    startMode: ContainerStartMode = ContainerStartMode.USE_RUNNING_ELSE_CREATE,
    sudo: Boolean = true
): ProvResult = requireLast {
    val dockerCmd = if (sudo) "sudo docker " else "docker "

    if (startMode == ContainerStartMode.CREATE_NEW_KILL_EXISTING) {
        exitAndRmContainer(containerName)
    }
    if ((startMode == ContainerStartMode.CREATE_NEW_KILL_EXISTING) || (startMode == ContainerStartMode.CREATE_NEW_FAIL_IF_EXISTING)) {
        if (!cmd(dockerCmd + "run -dit --name=$containerName $imageName").success) {
            throw RuntimeException("could not start docker")
        }
    } else if (startMode == ContainerStartMode.USE_RUNNING_ELSE_CREATE) {
        val r =
            cmd(dockerCmd + "inspect -f '{{.State.Running}}' $containerName")
        if (!r.success || "false\n" == r.out) {
            cmd(dockerCmd + "rm -f $containerName")
            cmd(dockerCmd + "run -dit --name=$containerName $imageName")
        }
    }
    ProvResult(containerRuns(containerName, sudo))
}


fun UbuntuProv.containerRunsPlatform(containerName: String, sudo: Boolean = true): Boolean {
    val dockerCmd = if (sudo) "sudo docker " else "docker "
    return cmdNoEval(dockerCmd + "inspect -f '{{.State.Running}}' $containerName").out?.equals("true\n") ?: false
}


fun UbuntuProv.runContainerPlatform(
    containerName: String = "defaultProvContainer",
    imageName: String = "ubuntu",
    sudo: Boolean = true
) = def {
    val dockerCmd = if (sudo) "sudo docker " else "docker "
    cmd(dockerCmd + "run -dit --name=$containerName $imageName")
}


fun UbuntuProv.containerExecPlatform(containerName: String, cmd: String, sudo: Boolean = true) = def {
    val dockerCmd = if (sudo) "sudo docker " else "docker "
    cmd(dockerCmd + "exec $containerName $cmd")
}


fun UbuntuProv.dockerBuildImagePlatform(image: DockerImage, skipIfExisting: Boolean, sudo: Boolean): ProvResult {
    val dockerCmd = if (sudo) "sudo docker " else "docker "

    if (skipIfExisting && dockerImageExists(image.imageName())) {
        return ProvResult(true)
    }

    val path = hostUserHome() + "tmp_docker_img" + fileSeparator()

    if (!xec("test", "-d", path).success) {
        cmd("cd ${hostUserHome()} && mkdir tmp_docker_img")
    }

    cmd("cd $path && printf '${image.imageText().escapeSingleQuote()}' > Dockerfile")

    return cmd("cd $path && "+dockerCmd+"build --tag ${image.imageName()} .")
}


fun UbuntuProv.dockerImageExistsPlatform(imageName: String, sudo: Boolean): Boolean {
    val dockerCmd = if (sudo) "sudo docker " else "docker "

    return (cmd(dockerCmd + "images $imageName -q").out != "")
}


fun UbuntuProv.exitAndRmContainerPlatform(
    containerName: String,
    sudo: Boolean
) = requireAll {
    val dockerCmd = if (sudo) "sudo docker " else "docker "

    if (containerRuns(containerName)) {
        cmd(dockerCmd + "stop $containerName")
    }
    cmd(dockerCmd + "rm $containerName")
}
