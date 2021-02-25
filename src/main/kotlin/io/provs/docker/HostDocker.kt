package io.provs.docker

import io.provs.Prov
import io.provs.ProvResult
import io.provs.docker.images.DockerImage
import io.provs.docker.platforms.*
import io.provs.platforms.UbuntuProv
import io.provs.processors.ContainerStartMode


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
    sudo: Boolean = true
) : ProvResult {
    if (this is UbuntuProv) {
        return this.provideContainerPlatform(containerName, imageName, startMode, sudo)
    } else {
        throw RuntimeException("docker not yet supported for " + (this as UbuntuProv).javaClass)
    }
}


fun Prov.containerRuns(containerName: String, sudo: Boolean = true) : Boolean {
    if (this is UbuntuProv) {
        return this.containerRunsPlatform(containerName, sudo)
    } else {
        throw RuntimeException("docker not yet supported for " + (this as UbuntuProv).javaClass)
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
        throw RuntimeException("docker not yet supported for " + (this as UbuntuProv).javaClass)
    }
}


fun Prov.dockerBuildImage(image: DockerImage, skipIfExisting: Boolean = true, sudo: Boolean = true) : ProvResult {
    if (this is UbuntuProv) {
        return this.dockerBuildImagePlatform(image, skipIfExisting, sudo)
    } else {
        throw RuntimeException("docker not yet supported for " + (this as UbuntuProv).javaClass)
    }
}


fun Prov.dockerImageExists(imageName: String, sudo: Boolean = true) : Boolean {
    if (this is UbuntuProv) {
        return this.dockerImageExistsPlatform(imageName, sudo)
    } else {
        throw RuntimeException("docker not yet supported for " + (this as UbuntuProv).javaClass)
    }
}


fun Prov.exitAndRmContainer(
    containerName: String,
    sudo: Boolean = true
) : ProvResult {
    if (this is UbuntuProv) {
        return this.exitAndRmContainerPlatform(containerName, sudo)
    } else {
        throw RuntimeException("docker not yet supported for " + (this as UbuntuProv).javaClass)
    }
}
