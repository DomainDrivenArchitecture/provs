package org.domaindrivenarchitecture.provs.framework.ubuntu.secret.domain

import org.domaindrivenarchitecture.provs.framework.core.Secret
import kotlinx.serialization.Serializable
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.domain.secretSources.EnvSecretSource
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.domain.secretSources.FileSecretSource
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.domain.secretSources.GopassSecretSource
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.domain.secretSources.PassSecretSource
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.domain.secretSources.PlainSecretSource
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.domain.secretSources.PromptSecretSource


@Serializable
abstract class SecretSource(protected val parameter: String) {
    abstract fun secret() : Secret
    abstract fun secretNullable() : Secret?
}


@Serializable
enum class SecretSourceType {

    PLAIN, FILE, PROMPT, PASS, GOPASS, ENV;

    fun secret(parameter: String) : Secret {
        return when (this) {
            PLAIN -> PlainSecretSource(parameter).secret()
            FILE -> FileSecretSource(parameter).secret()
            PROMPT -> PromptSecretSource().secret()
            PASS -> PassSecretSource(parameter).secret()
            GOPASS -> GopassSecretSource(parameter).secret()
            ENV -> EnvSecretSource(parameter).secret()
        }
    }
}


@Serializable
data class SecretSupplier(private val source: SecretSourceType, val parameter: String) {
    fun secret(): Secret {
        return source.secret(parameter)
    }
}
