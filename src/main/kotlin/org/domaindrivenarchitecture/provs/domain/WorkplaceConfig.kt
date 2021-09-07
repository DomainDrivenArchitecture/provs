package org.domaindrivenarchitecture.provs.domain

import com.charleskorn.kaml.Yaml
import org.domaindrivenarchitecture.provs.ubuntu.keys.KeyPairSource
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.domaindrivenarchitecture.provs.core.tags.Api
import java.io.*


@Serializable
class WorkplaceConfig(
    val type: WorkplaceType = WorkplaceType.MINIMAL,
    val ssh: KeyPairSource? = null,
    val gpg: KeyPairSource? = null,
    val gitUserName: String? = null,
    val gitEmail: String? = null,
)

