package org.domaindrivenarchitecture.provs.desktop.domain

import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.domain.KeyPairSource
import kotlinx.serialization.Serializable
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.domain.SshKeyPairSource


@Serializable
data class DesktopConfig(
    val ssh: SshKeyPairSource? = null,
    val gpg: KeyPairSource? = null,
    val gitUserName: String? = null,
    val gitEmail: String? = null,
)
