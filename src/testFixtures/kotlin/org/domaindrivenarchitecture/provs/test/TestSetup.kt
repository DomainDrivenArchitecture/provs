package org.domaindrivenarchitecture.provs.test

import org.domaindrivenarchitecture.provs.framework.core.ProgressType
import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.docker.containerRuns
import org.domaindrivenarchitecture.provs.framework.core.docker.dockerImageExists
import org.domaindrivenarchitecture.provs.framework.core.docker.dockerProvideImage
import org.domaindrivenarchitecture.provs.framework.core.docker.dockerimages.UbuntuPlusUser
import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerUbuntuHostProcessor

val testDockerWithSudo = ("true" != System.getProperty("testdockerwithoutsudo")?.lowercase())

const val defaultTestContainerName = "provs_test"

private lateinit var prov: Prov

fun defaultTestContainer(startMode: ContainerStartMode = ContainerStartMode.USE_RUNNING_ELSE_CREATE): Prov {
    if (!::prov.isInitialized || !testLocal().containerRuns(defaultTestContainerName)) { prov = initDefaultTestContainer(startMode) }
    return prov
}

private fun initDefaultTestContainer(startMode: ContainerStartMode): Prov {
    val image = UbuntuPlusUser()
    val localProv = testLocal()
    if (!localProv.dockerImageExists(image.imageName(), testDockerWithSudo)) {
        localProv.dockerProvideImage(image, sudo = testDockerWithSudo)
    }

    val containerProv = Prov.newInstance(
        ContainerUbuntuHostProcessor(
            defaultTestContainerName,
            startMode = startMode,
            sudo = testDockerWithSudo,
            dockerImage = image.imageName()
        ),
        progressType = ProgressType.NONE
    )

    containerProv.sh("""
            sudo apt-get update
            sudo apt-get upgrade -qqq
        """.trimIndent())

    return containerProv
}

fun testLocal(): Prov {
    return Prov.newInstance(name = "testing", progressType = ProgressType.NONE)
}