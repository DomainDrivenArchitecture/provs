package org.domaindrivenarchitecture.provs.framework.ubuntu.user

import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.KeyPairSource
import kotlinx.serialization.Serializable
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.SshKeyPairSource


@Serializable
class UserConfig(
    val userName: String,
    val gitEmail: String? = null,
    val gpg: KeyPairSource? = null,
    val ssh: SshKeyPairSource? = null)
