package org.domaindrivenarchitecture.provs.extensions.server_software.k3s.infrastructure.apple

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.ProvResult


fun Prov.checkAppleService(kubeCtlCmd: String = "kubectl") = task {
    val ip = cmd(kubeCtlCmd + " get svc apple-service -o jsonpath=\"{.spec.clusterIP}\"\n").out
    val port = cmd(kubeCtlCmd + " get svc apple-service -o jsonpath=\"{.spec.ports[0].port}\"\n").out
    if (ip == null || port == null) {
        return@task ProvResult(false)
    }
    val apple = cmd("curl -m 30 $ip:$port").out
    if ("apple" == apple) {
        ProvResult(true)
    } else {
        ProvResult(false, err = "Apple service did not return \"apple\" but instead: " + apple)
    }
}


fun appleConfig() =
    """
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
