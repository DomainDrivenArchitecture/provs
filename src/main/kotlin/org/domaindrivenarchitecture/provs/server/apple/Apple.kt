package org.domaindrivenarchitecture.provs.server.apple

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.core.remote
import org.domaindrivenarchitecture.provs.framework.core.repeatTaskUntilSuccess


/**
 * Checks if URL "$host/apple" is available and return text "apple"
 */
fun Prov.checkAppleService(host: String = "127.0.0.1") = requireLast {
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


/**
 * Example how to install k3s and add apple
 */
fun main() {

    val host = "123.34.56.78"

    remote(host, "root").task {
        //installK3sServer(tlsHost = host)
        //applyK3sConfig(appleConfig())

        // optional check
        checkAppleService(host)
    }
}
