package org.domaindrivenarchitecture.provs.ubuntu.secret.secretSources

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.ubuntu.secret.SecretSource


/**
 * Retrieve secret from passwordstore on Unix
 */
class PassSecretSource(path: String) : SecretSource(path) {
    override fun secret(): Secret {
        val p = Prov.newInstance(name = "PassSecretSource")
        return p.getSecret("pass " + input) ?: throw Exception("Failed to get secret.")
    }
    override fun secretNullable(): Secret? {
        val p = Prov.newInstance(name = "PassSecretSource")
        return p.getSecret("pass " + input)
    }
}