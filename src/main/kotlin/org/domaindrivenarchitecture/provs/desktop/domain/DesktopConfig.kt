package org.domaindrivenarchitecture.provs.desktop.domain

import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.KeyPairSource
import kotlinx.serialization.Serializable


@Serializable
data class DesktopConfig(
    val ssh: KeyPairSource? = null,
    val gpg: KeyPairSource? = null,
    val gitUserName: String? = null,
    val gitEmail: String? = null,
)
