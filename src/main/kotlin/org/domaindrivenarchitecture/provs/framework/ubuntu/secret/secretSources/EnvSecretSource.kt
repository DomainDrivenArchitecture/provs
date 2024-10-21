package org.domaindrivenarchitecture.provs.framework.ubuntu.secret.secretSources

import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.SecretSource


/**
 * Reads secret from a local environment variable
 */
class EnvSecretSource(varName: String) : SecretSource(varName) {
    override fun secret(): Secret {
        return secretNullable() ?: throw Exception("Failed to get secret from environment variable: $parameter")
    }
    override fun secretNullable(): Secret? {
        val secret = System.getenv(parameter)
        return if (secret == null) null else Secret(secret)
    }
}