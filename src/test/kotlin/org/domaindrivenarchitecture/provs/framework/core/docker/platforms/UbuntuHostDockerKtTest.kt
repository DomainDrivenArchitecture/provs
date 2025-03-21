package org.domaindrivenarchitecture.provs.framework.core.docker.platforms

import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.core.docker.containerRuns
import org.domaindrivenarchitecture.provs.framework.core.docker.exitAndRmContainer
import org.domaindrivenarchitecture.provs.framework.core.docker.runContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.test.tags.NonCi
import org.domaindrivenarchitecture.provs.test.testLocal
import org.junit.jupiter.api.Assertions.assertEquals

internal class UbuntuHostDockerKtTest {

    @NonCi
    @ContainerTest
    fun runAndCheckAndExitContainer() {
        // when
        val containerName = "testContainer"
        val result = testLocal().task {
            runContainer(containerName)
            addResult(containerRuns(containerName))

            exitAndRmContainer(containerName)
        }

        // then
        assertEquals(ProvResult(true), result)
    }
}