package org.domaindrivenarchitecture.provs.framework.ubuntu.user.domain

import kotlinx.serialization.Serializable
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.domain.KeyPairSource
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.domain.SshKeyPairSource

@Serializable
class UserConfig(
    val userName: String,
    val gitEmail: String? = null,
    val gpg: KeyPairSource? = null,
    val ssh: SshKeyPairSource? = null)