package org.domaindrivenarchitecture.provs.ubuntu.secret

import org.domaindrivenarchitecture.provs.core.Secret
import org.domaindrivenarchitecture.provs.ubuntu.secret.secretSources.*
import kotlinx.serialization.Serializable


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
@Suppress("unused")  // for use in other projects
class SecretSupplier(private val source: SecretSourceType, val parameter: String) {
    fun secret(): Secret {
        return source.secret(parameter)
    }
}

