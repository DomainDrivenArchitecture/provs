package org.domaindrivenarchitecture.provs.framework.ubuntu.secret.secretSources

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.SecretSource


/**
 * Retrieve secret from gopass
 */
class GopassSecretSource(path: String) : SecretSource(path) {
    override fun secret(): Secret {
        return secretNullable() ?: throw Exception("Failed to get \"$input\" secret from gopass.")
    }
    override fun secretNullable(): Secret? {
        val p = Prov.newInstance(name = "GopassSecretSource for $input")
        return p.getSecret("gopass show -f " + input)
    }
}