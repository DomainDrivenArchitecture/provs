import org.domaindrivenarchitecture.provs.core.remote
import org.domaindrivenarchitecture.provs.extensions.server_software.k3s.applyK3sConfig
import org.domaindrivenarchitecture.provs.extensions.server_software.k3s.infrastructure.apple.appleConfig
import org.domaindrivenarchitecture.provs.extensions.server_software.k3s.infrastructure.apple.checkAppleService
import org.domaindrivenarchitecture.provs.extensions.server_software.k3s.installK3sServer
import org.domaindrivenarchitecture.provs.ubuntu.secret.secretSources.PromptSecretSource

fun main() {

    val host = "192.168.56.141"
    val remoteUser = "usr"
    val passwordK3sUser = PromptSecretSource("Enter Password").secret()

    val prov = remote(host, remoteUser, passwordK3sUser)
    // alternatively run local: val prov = local()

    prov.task {

        installK3sServer()

        // print pods for information purpose
        println(cmd("sudo k3s kubectl get pods --all-namespaces").out)

        applyK3sConfig(appleConfig())

        // print pods for information purpose
        println(cmd("sudo k3s kubectl get services").out)

        checkAppleService()
    }
}