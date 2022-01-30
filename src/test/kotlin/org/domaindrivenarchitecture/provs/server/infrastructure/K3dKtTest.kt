package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.*
import org.domaindrivenarchitecture.provs.framework.core.docker.containerExec
import org.domaindrivenarchitecture.provs.framework.core.docker.provideContainer
import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.server.domain.applyK8sConfig
import org.domaindrivenarchitecture.provs.server.domain.installK3sAsContainers
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.test.tags.NonCi
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class K3dKtTest {

    @Test
    @Disabled // remove line and execute manually as this test may take several minutes
    @ContainerTest
    @NonCi
    fun installK3sAsContainers() {

        // given
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


/**
 * Checks if URL "$host/apple" is available and return text "apple"
 */
private fun Prov.checkAppleService(host: String = "127.0.0.1") = requireLast {
    // repeat required as curl may return with "empty reply from server" or with "Recv failure: Connection reset by peer"
    val res = repeatTaskUntilSuccess(12, 10) {
        cmd("curl -m 30 $host/apple")
    }.out?.trim()

    if ("apple" == res) {
        ProvResult(true, out = res)
    } else {
        ProvResult(false, err = "Url $host/apple did not return text \"apple\" but returned: $res")
    }
}


fun appleConfig() =
    """
kind: Ingress
apiVersion: networking.k8s.io/v1
metadata:
  name: apple-ingress
  annotations:
    kubernetes.io/ingress.class: "traefik"
spec:
  rules:
  - http:
      paths:
      - path: /apple
        pathType: Prefix
        backend:
          service: 
            name: apple-service
            port: 
              number: 5678
---

kind: Pod
apiVersion: v1
metadata:
  name: apple-app
  labels:
    app: apple
spec:
  containers:
    - name: apple-app
      image: hashicorp/http-echo
      args:
        - "-text=apple"
---

kind: Service
apiVersion: v1
metadata:
  name: apple-service
spec:
  selector:
    app: apple
  ports:
    - port: 5678 # Default port for image
    """
