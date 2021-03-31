package io.provs.test

import io.provs.Prov
import io.provs.processors.ContainerStartMode
import io.provs.processors.ContainerUbuntuHostProcessor

val DEFAULT_START_MODE_TEST_CONTAINER = ContainerStartMode.USE_RUNNING_ELSE_CREATE

val testDockerWithSudo = "true" != System.getProperty("testdockerwithoutsudo")?.toLowerCase()

const val defaultTestContainerName = "provs_test"

fun defaultTestContainer(imageName: String = "ubuntu"): Prov {
        return Prov.newInstance(
            ContainerUbuntuHostProcessor(
                defaultTestContainerName,
                startMode = DEFAULT_START_MODE_TEST_CONTAINER,
                sudo = testDockerWithSudo,
                dockerImage = imageName
            )
        )
}
