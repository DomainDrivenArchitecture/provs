package org.domaindrivenarchitecture.provs.ubuntu.secret.secretSources

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.Secret
import org.domaindrivenarchitecture.provs.ubuntu.secret.SecretSource


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