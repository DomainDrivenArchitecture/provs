package org.domaindrivenarchitecture.provs.framework.core.docker.platforms

import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.core.docker.containerRuns
import org.domaindrivenarchitecture.provs.framework.core.docker.dockerImageExists
import org.domaindrivenarchitecture.provs.framework.core.docker.dockerimages.DockerImage
import org.domaindrivenarchitecture.provs.framework.core.docker.exitAndRmContainer
import org.domaindrivenarchitecture.provs.framework.core.escapeSingleQuote
import org.domaindrivenarchitecture.provs.framework.core.fileSeparator
import org.domaindrivenarchitecture.provs.framework.core.hostUserHome
import org.domaindrivenarchitecture.provs.framework.core.platforms.UbuntuProv
import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerStartMode


fun UbuntuProv.provideContainerPlatform(
    containerName: String,
    imageName: String = "ubuntu",
    startMode: ContainerStartMode = ContainerStartMode.USE_RUNNING_ELSE_CREATE,
    sudo: Boolean = true,
    options: String = "",
    command: String =""
): ProvResult = requireLast {
    val dockerCmd = dockerCommand(sudo)

    if (startMode == ContainerStartMode.CREATE_NEW_KILL_EXISTING) {
        exitAndRmContainer(containerName)
    }

    val runCommand = dockerCmd + "run -dit $options --name=$containerName $imageName $command"

    if ((startMode == ContainerStartMode.CREATE_NEW_KILL_EXISTING) || (startMode == ContainerStartMode.CREATE_NEW_FAIL_IF_EXISTING)) {
        if (!cmd(runCommand).success) {
            throw RuntimeException("could not start docker")
        }
    } else if (startMode == ContainerStartMode.USE_RUNNING_ELSE_CREATE) {
        val runCheckResult = cmdNoEval(dockerCmd + "inspect -f '{{.State.Running}}' $containerName")

        // if either container not found or container found but not running => remove container and start again
        if (!runCheckResult.success || "false\n" == runCheckResult.out) {
            cmdNoEval(dockerCmd + "rm -f $containerName")
            cmd(runCommand)
        }
    }
    ProvResult(containerRuns(containerName, sudo))
}


fun UbuntuProv.containerRunsPlatform(containerName: String, sudo: Boolean = true): Boolean {
    val dockerCmd = dockerCommand(sudo)
    return cmdNoEval(dockerCmd + "inspect -f '{{.State.Running}}' $containerName").out?.equals("true\n") ?: false
}


fun UbuntuProv.runContainerPlatform(
    containerName: String = "defaultProvContainer",
    imageName: String = "ubuntu",
    sudo: Boolean = true
) = def {
    val dockerCmd = dockerCommand(sudo)
    cmd(dockerCmd + "run -dit --name=$containerName $imageName")
}


fun UbuntuProv.containerExecPlatform(containerName: String, cmd: String, sudo: Boolean = true) = def {
    val dockerCmd = dockerCommand(sudo)
    cmd(dockerCmd + "exec $containerName $cmd")
}


fun UbuntuProv.dockerProvideImagePlatform(image: DockerImage, skipIfExisting: Boolean, sudo: Boolean): ProvResult {
    val dockerCmd = dockerCommand(sudo)

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
    val dockerCmd = dockerCommand(sudo)

    return (cmdNoEval(dockerCmd + "images $imageName -q").out != "")
}


fun UbuntuProv.exitAndRmContainerPlatform(
    containerName: String,
    sudo: Boolean
) = requireAll {
    val dockerCmd = dockerCommand(sudo)

    if (containerRuns(containerName)) {
        cmd(dockerCmd + "stop $containerName")
    }
    cmd(dockerCmd + "rm $containerName")
}

private fun dockerCommand(sudo: Boolean) = if (sudo) "sudo docker " else "docker "
