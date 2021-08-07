package org.domaindrivenarchitecture.provs.ubuntu.secret.secretSources

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.Secret
import org.domaindrivenarchitecture.provs.ubuntu.secret.SecretSource


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