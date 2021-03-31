package io.provs.test

import io.provs.Prov
import io.provs.docker.dockerImageExists
import io.provs.docker.dockerProvideImage
import io.provs.docker.images.UbuntuPlusUser
import io.provs.local
import io.provs.processors.ContainerStartMode
import io.provs.processors.ContainerUbuntuHostProcessor

val DEFAULT_START_MODE_TEST_CONTAINER = ContainerStartMode.USE_RUNNING_ELSE_CREATE

val testDockerWithSudo = !"true".equals(System.getProperty("testdockerwithoutsudo")?.toLowerCase())

const val defaultTestContainerName = "provs_test"

fun defaultTestContainer(): Prov {
    val image = UbuntuPlusUser()
    val prov = local()
    if (!prov.dockerImageExists(image.imageName(), testDockerWithSudo)) {
        prov.dockerProvideImage(image, sudo = testDockerWithSudo)
    }

    return Prov.newInstance(
        ContainerUbuntuHostProcessor(
            defaultTestContainerName,
            startMode = DEFAULT_START_MODE_TEST_CONTAINER,
            sudo = testDockerWithSudo,
            dockerImage = image.imageName()
        )
    )
}
