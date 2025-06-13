package org.domaindrivenarchitecture.provs.framework.ubuntu.secret.domain.secretSources

import org.domaindrivenarchitecture.provs.framework.core.ProgressType
import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.domain.SecretSource


/**
 * Retrieve secret from gopass
 */
class GopassSecretSource(path: String) : SecretSource(path) {
    override fun secret(): Secret {
        return secretNullable() ?: throw Exception("Failed to get \"$parameter\" secret from gopass.")
    }
    override fun secretNullable(): Secret? {
        val p = Prov.newInstance(name = "GopassSecretSource for $parameter", progressType = ProgressType.NONE)
        return p.getSecret("gopass show -f $parameter", true)
    }
}