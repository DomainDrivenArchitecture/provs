package org.domaindrivenarchitecture.provs.workplace.domain

import org.domaindrivenarchitecture.provs.ubuntu.keys.KeyPairSource
import kotlinx.serialization.Serializable


@Serializable
class WorkplaceConfig(
    val type: WorkplaceType = WorkplaceType.MINIMAL,
    val ssh: KeyPairSource? = null,
    val gpg: KeyPairSource? = null,
    val gitUserName: String? = null,
    val gitEmail: String? = null,
)

