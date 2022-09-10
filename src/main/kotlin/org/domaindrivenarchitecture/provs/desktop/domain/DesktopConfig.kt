package org.domaindrivenarchitecture.provs.desktop.domain

import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.KeyPairSource
import kotlinx.serialization.Serializable
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.SshKeyPairSource


@Serializable
data class DesktopConfig(
    val ssh: SshKeyPairSource? = null,
    val gpg: KeyPairSource? = null,
    val gitUserName: String? = null,
    val gitEmail: String? = null,
)
