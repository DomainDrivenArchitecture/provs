package io.provs.ubuntu.keys

import io.provs.core.Prov
import io.provs.core.ProvResult
import io.provs.core.Secret
import io.provs.ubuntu.keys.base.configureGpgKeys
import io.provs.ubuntu.keys.base.configureSshKeys
import io.provs.ubuntu.secret.SecretSourceType
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

