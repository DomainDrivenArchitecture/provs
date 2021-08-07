package io.provs.ubuntu.secret.secretSources

import io.provs.Prov
import io.provs.Secret
import io.provs.ubuntu.secret.SecretSource


/**
 * Retrieve secret from a file
 */
class FileSecretSource(fqFileName: String) : SecretSource(fqFileName) {

    override fun secret(): Secret {
        val p = Prov.newInstance(name = "FileSecretSource")
        return p.getSecret("cat " + input) ?: throw Exception("Failed to get secret.")
    }

    override fun secretNullable(): Secret? {
        val p = Prov.newInstance(name = "FileSecretSource")
        return p.getSecret("cat " + input)
    }
}