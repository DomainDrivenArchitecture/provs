package io.provs.docker.platforms

import io.provs.ProvResult
import io.provs.docker.containerRuns
import io.provs.docker.exitAndRmContainer
import io.provs.docker.runContainer
import io.provs.test.tags.NonCi
import io.provs.test.testLocal
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