package io.provs.test

import io.provs.core.ProgressType
import io.provs.core.Prov
import io.provs.core.docker.dockerImageExists
import io.provs.core.docker.dockerProvideImage
import io.provs.core.docker.dockerimages.UbuntuPlusUser
import io.provs.core.processors.ContainerStartMode
import io.provs.core.processors.ContainerUbuntuHostProcessor

val testDockerWithSudo = !"true".equals(System.getProperty("testdockerwithoutsudo")?.toLowerCase())

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