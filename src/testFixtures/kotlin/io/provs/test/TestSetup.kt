package io.provs.test

import io.provs.core.ProgressType
import io.provs.core.Prov
import io.provs.core.docker.dockerImageExists
import io.provs.core.docker.dockerProvideImage
import io.provs.core.docker.dockerimages.UbuntuPlusUser
import io.provs.core.processors.ContainerStartMode
import io.provs.core.processors.ContainerUbuntuHostProcessor

val DEFAULT_START_MODE_TEST_CONTAINER = ContainerStartMode.USE_RUNNING_ELSE_CREATE

val testDockerWithSudo = !"true".equals(System.getProperty("testdockerwithoutsudo")?.toLowerCase())

const val defaultTestContainerName = "provs_test"

fun defaultTestContainer(): Prov {
    val image = UbuntuPlusUser()
    val prov = testLocal()
    if (!prov.dockerImageExists(image.imageName(), testDockerWithSudo)) {
        prov.dockerProvideImage(image, sudo = testDockerWithSudo)
    }

    return Prov.newInstance(
        ContainerUbuntuHostProcessor(
            defaultTestContainerName,
            startMode = DEFAULT_START_MODE_TEST_CONTAINER,
            sudo = testDockerWithSudo,
            dockerImage = image.imageName()
        ),
        progressType = ProgressType.NONE
    )
}

fun testLocal(): Prov {
    return Prov.newInstance(name = "testing", progressType = ProgressType.NONE)
}