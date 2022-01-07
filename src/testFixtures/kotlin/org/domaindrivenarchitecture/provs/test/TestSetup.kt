package org.domaindrivenarchitecture.provs.test

import org.domaindrivenarchitecture.provs.core.ProgressType
import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.docker.dockerImageExists
import org.domaindrivenarchitecture.provs.core.docker.dockerProvideImage
import org.domaindrivenarchitecture.provs.core.docker.dockerimages.UbuntuPlusUser
import org.domaindrivenarchitecture.provs.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.core.processors.ContainerUbuntuHostProcessor

val testDockerWithSudo = ("true" != System.getProperty("testdockerwithoutsudo")?.lowercase())

const val defaultTestContainerName = "provs_test"

fun defaultTestContainer(startMode: ContainerStartMode = ContainerStartMode.USE_RUNNING_ELSE_CREATE): Prov {
    val image = UbuntuPlusUser()
    val prov = testLocal()
    if (!prov.dockerImageExists(image.imageName(), testDockerWithSudo)) {
        prov.dockerProvideImage(image, sudo = testDockerWithSudo)
    }

    return Prov.newInstance(
        ContainerUbuntuHostProcessor(
            defaultTestContainerName,
            startMode = startMode,
            sudo = testDockerWithSudo,
            dockerImage = image.imageName()
        ),
        progressType = ProgressType.NONE
    )
}

fun testLocal(): Prov {
    return Prov.newInstance(name = "testing", progressType = ProgressType.NONE)
}