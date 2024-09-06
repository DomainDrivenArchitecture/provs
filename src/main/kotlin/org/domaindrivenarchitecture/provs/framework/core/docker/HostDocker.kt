package org.domaindrivenarchitecture.provs.framework.core.docker

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.core.docker.dockerimages.DockerImage
import org.domaindrivenarchitecture.provs.framework.core.docker.platforms.*
import org.domaindrivenarchitecture.provs.framework.core.platforms.UbuntuProv
import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerStartMode


private const val DOCKER_NOT_SUPPORTED = "docker not yet supported for "

/**
 * Builds a docker image if not yet existing.
 */
fun Prov.dockerProvideImage(image: DockerImage, skipIfExisting: Boolean = true, sudo: Boolean = true) : ProvResult {
    if (this is UbuntuProv) {
        return this.dockerProvideImagePlatform(image, skipIfExisting, sudo)
    } else {
        throw RuntimeException(DOCKER_NOT_SUPPORTED + this.javaClass)
    }
}

/**
 * Returns true if the specified docker image exists.
 */
fun Prov.dockerImageExists(imageName: String, sudo: Boolean = true) : Boolean {
    if (this is UbuntuProv) {
        return this.dockerImageExistsPlatform(imageName, sudo)
    } else {
        throw RuntimeException(DOCKER_NOT_SUPPORTED + this.javaClass)
    }
}

/**
 * Creates and runs a new container with name _containerName_ for image _imageName_ if not yet existing.
 * In case the container already exists, the parameter _startMode_ determines
 * if the running container is just kept (default behavior)
 * or if the running container is stopped and removed and a new container is created
 * or if the method results in a failure result.
 */
fun Prov.provideContainer(
    containerName: String,
    imageName: String = "ubuntu",
    startMode: ContainerStartMode = ContainerStartMode.USE_RUNNING_ELSE_CREATE,
    sudo: Boolean = true,
    options: String = "",
    command: String = ""
) : ProvResult {
    if (this is UbuntuProv) {
        return this.provideContainerPlatform(containerName, imageName, startMode, sudo, options, command)
    } else {
        throw RuntimeException(DOCKER_NOT_SUPPORTED + this.javaClass)
    }
}


fun Prov.containerRuns(containerName: String, sudo: Boolean = true) : Boolean {
    if (this is UbuntuProv) {
        return this.containerRunsPlatform(containerName, sudo)
    } else {
        throw RuntimeException(DOCKER_NOT_SUPPORTED + this.javaClass)
    }
}


fun Prov.runContainer(
    containerName: String = "provs_default",
    imageName: String = "ubuntu",
    sudo: Boolean = true
) : ProvResult {
    if (this is UbuntuProv) {
        return this.runContainerPlatform(containerName, imageName, sudo)
    } else {
        throw RuntimeException(DOCKER_NOT_SUPPORTED + this.javaClass)
    }
}


fun Prov.exitAndRmContainer(
    containerName: String,
    sudo: Boolean = true
) : ProvResult {
    if (this is UbuntuProv) {
        return this.exitAndRmContainerPlatform(containerName, sudo)
    } else {
        throw RuntimeException(DOCKER_NOT_SUPPORTED + this.javaClass)
    }
}


@Suppress("unused")
fun Prov.containerExec(containerName: String, cmd: String, sudo: Boolean = true): ProvResult {
    if (this is UbuntuProv) {
        return this.containerExecPlatform(containerName, cmd, sudo)
    } else {
        throw RuntimeException(DOCKER_NOT_SUPPORTED + this.javaClass)
    }
}


