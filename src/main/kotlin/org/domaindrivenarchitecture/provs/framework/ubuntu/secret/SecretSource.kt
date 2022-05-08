package org.domaindrivenarchitecture.provs.framework.ubuntu.secret

import org.domaindrivenarchitecture.provs.framework.core.Secret
import kotlinx.serialization.Serializable
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.secretSources.*


@Serializable
abstract class SecretSource(protected val input: String) {
    abstract fun secret() : Secret
    abstract fun secretNullable() : Secret?
}


@Serializable
enum class SecretSourceType() {

    PLAIN, FILE, PROMPT, PASS, GOPASS;

    fun secret(input: String) : Secret {
        return when (this) {
            PLAIN -> PlainSecretSource(input).secret()
            FILE -> FileSecretSource(input).secret()
            PROMPT -> PromptSecretSource().secret()
            PASS -> PassSecretSource(input).secret()
            GOPASS -> GopassSecretSource(input).secret()
        }
    }
}


@Serializable
class SecretSupplier(private val source: SecretSourceType, val parameter: String) {
    fun secret(): Secret {
        return source.secret(parameter)
    }
}
