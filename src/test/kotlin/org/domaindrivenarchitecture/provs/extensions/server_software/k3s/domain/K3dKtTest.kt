package org.domaindrivenarchitecture.provs.extensions.server_software.k3s.domain

import org.domaindrivenarchitecture.provs.core.docker
import org.domaindrivenarchitecture.provs.core.docker.containerExec
import org.domaindrivenarchitecture.provs.core.docker.provideContainer
import org.domaindrivenarchitecture.provs.core.local
import org.domaindrivenarchitecture.provs.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.extensions.server_software.k3s.infrastructure.apple.appleConfig
import org.domaindrivenarchitecture.provs.extensions.server_software.k3s.infrastructure.apple.checkAppleService
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.test.tags.NonCi
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class K3dKtTest {

    @Test
    @ContainerTest
    @NonCi
    fun installK3sAsContainers() {

        val containerName = "alpine-docker-dind"
        local().task {
            provideContainer(
                containerName,
                "yobasystems/alpine-docker:dind-amd64",
                ContainerStartMode.CREATE_NEW_KILL_EXISTING,  // for re-create a potentially existing container
                sudo = false,
                options = "--privileged"
            )

            // alpine does not have bash pre-installed - but bash is currently required for provs
            containerExec(containerName, "sh -c \"apk add bash\"", sudo = false)
        }

        val result = docker(containerName, sudo = false).task {

            // given
            cmd("apk update")
            cmd("apk add sudo curl")
            task(
                "Install kubectl"
            ) {
                sh("""
                    curl -LO https://storage.googleapis.com/kubernetes-release/release/v1.18.0/bin/linux/amd64/kubectl
                    chmod +x ./kubectl
                    mv ./kubectl /usr/local/bin/kubectl
                    kubectl version --client
                """.trimIndent())
            }

            // when
            installK3sAsContainers()

            applyK8sConfig(appleConfig())

            cmd("kubectl wait --for=condition=ready --timeout=600s pod apple-app")
            checkAppleService()
        }

        // then
        assertTrue(result.success)
    }
}
