package io.provs

import io.provs.processors.ContainerStartMode
import io.provs.processors.ContainerUbuntuHostProcessor

val DEFAULT_START_MODE_TEST_CONTAINER = ContainerStartMode.USE_RUNNING_ELSE_CREATE

val testDockerWithSudo = !"true".equals(System.getProperty("testdockerwithoutsudo")?.toLowerCase())

const val defaultTestContainerName = "provs_test"

fun defaultTestContainer(): Prov {
        return Prov.newInstance(
            ContainerUbuntuHostProcessor(
                defaultTestContainerName,
                startMode = DEFAULT_START_MODE_TEST_CONTAINER,
                sudo = testDockerWithSudo
            )
        )
}
