package io.provs.ubuntu.secret.secretSources

import io.provs.core.Secret
import io.provs.ubuntu.secret.SecretSource


class PlainSecretSource(plainSecret: String) : SecretSource(plainSecret) {
    override fun secret(): Secret {
       return Secret(input)
    }
    override fun secretNullable(): Secret {
       return Secret(input)
    }
}