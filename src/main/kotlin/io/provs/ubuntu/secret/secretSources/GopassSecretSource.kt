package io.provs.ubuntu.secret.secretSources

import io.provs.core.Prov
import io.provs.core.Secret
import io.provs.ubuntu.secret.SecretSource


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