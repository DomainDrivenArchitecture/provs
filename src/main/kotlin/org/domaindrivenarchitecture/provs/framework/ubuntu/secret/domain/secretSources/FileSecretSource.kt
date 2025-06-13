package org.domaindrivenarchitecture.provs.framework.ubuntu.secret.domain.secretSources

import org.domaindrivenarchitecture.provs.framework.core.ProgressType
import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.domain.SecretSource


/**
 * Retrieve secret from a file
 */
class FileSecretSource(fqFileName: String) : SecretSource(fqFileName) {

    override fun secret(): Secret {
        val p = Prov.newInstance(name = "FileSecretSource", progressType = ProgressType.NONE)
        return p.getSecret("cat " + parameter) ?: throw Exception("Failed to get secret.")
    }

    override fun secretNullable(): Secret? {
        val p = Prov.newInstance(name = "FileSecretSource", progressType = ProgressType.NONE)
        return p.getSecret("cat " + parameter)
    }
}