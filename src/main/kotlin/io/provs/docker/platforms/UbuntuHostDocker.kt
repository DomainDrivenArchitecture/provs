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
    startMode: ContainerStartMode = ContainerStartMode.USE_RUNNING_ELSE_CREATE
): ProvResult = requireLast {
    if (startMode == ContainerStartMode.CREATE_NEW_KILL_EXISTING) {
        exitAndRmContainer(containerName)
    }
    if ((startMode == ContainerStartMode.CREATE_NEW_KILL_EXISTING) || (startMode == ContainerStartMode.CREATE_NEW_FAIL_IF_EXISTING)) {
        if (!cmd(
                "sudo docker run -dit --name=$containerName $imageName"
            ).success
        ) {
            throw RuntimeException("could not start docker")
        }
    } else if (startMode == ContainerStartMode.USE_RUNNING_ELSE_CREATE) {
        val r =
            cmd("sudo docker inspect -f '{{.State.Running}}' $containerName")
        if (!r.success || "false\n" == r.out) {
            cmd("sudo docker rm -f $containerName")
            cmd("sudo docker run -dit --name=$containerName $imageName")
        }
    }
    ProvResult(containerRuns(containerName))
}


fun UbuntuProv.containerRunsPlatform(containerName: String): Boolean {
    return cmdNoEval("sudo docker inspect -f '{{.State.Running}}' $containerName").out?.equals("true\n") ?: false
}


fun UbuntuProv.runContainerPlatform(
    containerName: String = "defaultProvContainer",
    imageName: String = "ubuntu"
) = def {
    cmd("sudo docker run -dit --name=$containerName $imageName")
}


fun UbuntuProv.containerExecPlatform(containerName: String, cmd: String) = def {
    cmd("sudo docker exec $containerName $cmd")
}


fun UbuntuProv.containerShPlatform(containerName: String, cmd: String) = def {
    containerExecPlatform(containerName, "sh -c \"${cmd.escapeDoubleQuote()}\"")
}


fun UbuntuProv.dockerBuildImagePlatform(image: DockerImage, skipIfExisting: Boolean): ProvResult {

    if (skipIfExisting && dockerImageExists(image.imageName())) {
        return ProvResult(true)
    }

    val path = hostUserHome() + "tmp_docker_img" + fileSeparator()

    if (!xec("test", "-d", path).success) {
        cmd("cd ${hostUserHome()} && mkdir tmp_docker_img")
    }

    cmd("cd $path && printf '${image.imageText().escapeSingleQuote()}' > Dockerfile")

    return cmd("cd $path && sudo docker build --tag ${image.imageName()} .")
}


fun UbuntuProv.dockerImageExistsPlatform(imageName: String): Boolean {
    return (cmd("sudo docker images $imageName -q").out != "")
}


fun UbuntuProv.exitAndRmContainerPlatform(
    containerName: String
) = requireAll {
    if (containerRuns(containerName)) {
        cmd("sudo docker stop $containerName")
    }
    cmd("sudo docker rm $containerName")
}
