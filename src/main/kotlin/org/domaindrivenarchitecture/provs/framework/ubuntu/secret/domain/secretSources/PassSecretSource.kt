package org.domaindrivenarchitecture.provs.framework.ubuntu.secret.domain.secretSources

import org.domaindrivenarchitecture.provs.framework.core.ProgressType
import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.domain.SecretSource


/**
 * Retrieve secret from passwordstore on Unix
 */
class PassSecretSource(path: String) : SecretSource(path) {
    override fun secret(): Secret {
        val p = Prov.newInstance(name = "PassSecretSource", progressType = ProgressType.NONE)
        return p.getSecret("pass " + parameter) ?: throw Exception("Failed to get secret.")
    }
    override fun secretNullable(): Secret? {
        val p = Prov.newInstance(name = "PassSecretSource", progressType = ProgressType.NONE)
        return p.getSecret("pass " + parameter)
    }
}