package org.domaindrivenarchitecture.provs.ubuntu.keys

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.ProvResult
import org.domaindrivenarchitecture.provs.core.Secret
import org.domaindrivenarchitecture.provs.ubuntu.keys.base.configureGpgKeys
import org.domaindrivenarchitecture.provs.ubuntu.keys.base.configureSshKeys
import org.domaindrivenarchitecture.provs.ubuntu.secret.SecretSourceType
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


/**
 * provisions gpg and/or ssh keys for the current user
 */
fun Prov.provisionKeysCurrentUser(gpgKeys: KeyPair? = null, sshKeys: KeyPair? = null) = requireAll {
    gpgKeys?.let { configureGpgKeys(it, true) }
    sshKeys?.let { configureSshKeys(it) }
    ProvResult(true)  // dummy
}

