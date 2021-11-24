package org.domaindrivenarchitecture.provs.ubuntu.user

import org.domaindrivenarchitecture.provs.ubuntu.keys.KeyPairSource
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.FileReader
import java.io.FileWriter


@Serializable
class UserConfig(val userName: String, val gitEmail: String? = null, val gpg: KeyPairSource? = null, val ssh: KeyPairSource? = null)
