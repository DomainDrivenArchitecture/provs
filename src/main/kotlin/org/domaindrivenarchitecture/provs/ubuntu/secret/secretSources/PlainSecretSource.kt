package org.domaindrivenarchitecture.provs.ubuntu.secret.secretSources

import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.ubuntu.secret.SecretSource


class PlainSecretSource(plainSecret: String) : SecretSource(plainSecret) {
    override fun secret(): Secret {
       return Secret(input)
    }
    override fun secretNullable(): Secret {
       return Secret(input)
    }
}