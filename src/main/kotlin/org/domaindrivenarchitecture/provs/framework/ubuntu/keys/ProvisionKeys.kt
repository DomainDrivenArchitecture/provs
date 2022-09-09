package org.domaindrivenarchitecture.provs.framework.ubuntu.keys

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base.configureGpgKeys
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base.configureSshKeys
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.SecretSourceType
import kotlinx.serialization.Serializable


open class KeyPair(val publicKey: Secret, val privateKey: Secret)


@Serializable
class KeyPairSource(val sourceType: SecretSourceType, val publicKey: String, val privateKey: String) {
    fun keyPair() : KeyPair {
        val pub = sourceType.secret(publicKey)
        val priv = sourceType.secret(privateKey)
        return KeyPair(pub, priv)
    }
}

@Serializable
class SshKeyPairSource(val sourceType: SecretSourceType, val publicKey: String, val privateKey: String) {
    fun keyPair() : SshKeyPair {
        val pub = sourceType.secret(publicKey)
        val priv = sourceType.secret(privateKey)
        return SshKeyPair(pub, priv)
    }
}

/**
 * provisions gpg and/or ssh keys for the current user
 */
fun Prov.provisionKeys(gpgKeys: KeyPair? = null, sshKeys: SshKeyPair? = null) = task {
    gpgKeys?.let { configureGpgKeys(it, true) }
    sshKeys?.let { configureSshKeys(it) }
    ProvResult(true)  // dummy
}

