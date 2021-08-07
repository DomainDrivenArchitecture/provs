package org.domaindrivenarchitecture.provs.core.docker.platforms

import org.domaindrivenarchitecture.provs.core.ProvResult
import org.domaindrivenarchitecture.provs.core.docker.containerRuns
import org.domaindrivenarchitecture.provs.core.docker.exitAndRmContainer
import org.domaindrivenarchitecture.provs.core.docker.runContainer
import org.domaindrivenarchitecture.provs.test.tags.NonCi
import org.domaindrivenarchitecture.provs.test.testLocal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS

internal class UbuntuHostDockerKtTest {

    @Test
    @EnabledOnOs(OS.LINUX)
    @NonCi
    fun runAndCheckAndExitContainer() {
        // when
        val containerName = "testContainer"
        val result = testLocal().requireAll {
            runContainer(containerName)
            addResultToEval(ProvResult(containerRuns(containerName)))

            exitAndRmContainer(containerName)
        }

        // then
        assertEquals(ProvResult(true), result)
    }
}