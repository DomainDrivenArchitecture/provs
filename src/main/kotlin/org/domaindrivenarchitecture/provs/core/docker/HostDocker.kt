package org.domaindrivenarchitecture.provs.core.docker

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.ProvResult
import org.domaindrivenarchitecture.provs.core.docker.dockerimages.DockerImage
import org.domaindrivenarchitecture.provs.core.docker.platforms.*
import org.domaindrivenarchitecture.provs.core.platforms.UbuntuProv
import org.domaindrivenarchitecture.provs.core.processors.ContainerStartMode

private const val DOCKER_NOT_SUPPORTED = "docker not yet supported for "

/**
 * Builds a docker image if not yet existing.
 */
fun Prov.dockerProvideImage(image: DockerImage, skipIfExisting: Boolean = true, sudo: Boolean = true) : ProvResult {
    if (this is UbuntuProv) {
        return this.dockerProvideImagePlatform(image, skipIfExisting, sudo)
    } else {
        throw RuntimeException(DOCKER_NOT_SUPPORTED + (this as UbuntuProv).javaClass)
    }
}

/**
 * Returns true if the specified docker image exists.
 */
fun Prov.dockerImageExists(imageName: String, sudo: Boolean = true) : Boolean {
    if (this is UbuntuProv) {
        return this.dockerImageExistsPlatform(imageName, sudo)
    } else {
        throw RuntimeException(DOCKER_NOT_SUPPORTED + (this as UbuntuProv).javaClass)
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
    options: String = ""
) : ProvResult {
    if (this is UbuntuProv) {
        return this.provideContainerPlatform(containerName, imageName, startMode, sudo, options)
    } else {
        throw RuntimeException(DOCKER_NOT_SUPPORTED + (this as UbuntuProv).javaClass)
    }
}


fun Prov.containerRuns(containerName: String, sudo: Boolean = true) : Boolean {
    if (this is UbuntuProv) {
        return this.containerRunsPlatform(containerName, sudo)
    } else {
        throw RuntimeException(DOCKER_NOT_SUPPORTED + (this as UbuntuProv).javaClass)
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
        throw RuntimeException(DOCKER_NOT_SUPPORTED + (this as UbuntuProv).javaClass)
    }
}


fun Prov.exitAndRmContainer(
    containerName: String,
    sudo: Boolean = true
) : ProvResult {
    if (this is UbuntuProv) {
        return this.exitAndRmContainerPlatform(containerName, sudo)
    } else {
        throw RuntimeException(DOCKER_NOT_SUPPORTED + (this as UbuntuProv).javaClass)
    }
}
