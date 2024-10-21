package org.domaindrivenarchitecture.provs.framework.ubuntu.secret.secretSources

import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.SecretSource


class PlainSecretSource(plainSecret: String) : SecretSource(plainSecret) {
    override fun secret(): Secret {
       return Secret(parameter)
    }
    override fun secretNullable(): Secret {
       return Secret(parameter)
    }
}