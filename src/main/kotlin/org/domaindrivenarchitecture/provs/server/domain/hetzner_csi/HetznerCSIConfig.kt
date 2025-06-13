package org.domaindrivenarchitecture.provs.server.domain.hetzner_csi

import kotlinx.serialization.Serializable
import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.domain.SecretSupplier

@Serializable
data class HetznerCSIConfig (
    val hcloudApiToken: SecretSupplier,
    val encryptionPassphrase: SecretSupplier,
) {
    fun resolveSecret(): HetznerCSIConfigResolved = HetznerCSIConfigResolved(this)
}

data class HetznerCSIConfigResolved(val configUnresolved: HetznerCSIConfig) {
    val hcloudApiToken: Secret = configUnresolved.hcloudApiToken.secret()
    val encryptionPassphrase: Secret = configUnresolved.encryptionPassphrase.secret()
}

@Serializable
data class HetznerCSIConfigHolder(
    val hetzner: HetznerCSIConfig
)