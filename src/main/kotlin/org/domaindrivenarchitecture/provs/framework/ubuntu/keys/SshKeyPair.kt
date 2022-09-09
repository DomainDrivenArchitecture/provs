package org.domaindrivenarchitecture.provs.framework.ubuntu.keys

import org.domaindrivenarchitecture.provs.framework.core.Secret

class SshKeyPair(publicKey: Secret, privateKey: Secret) : KeyPair(publicKey, privateKey) {

    val keyType = publicKey.plain().substringBefore(" ")

    val sshAlgorithmName =
        if (keyType.startsWith("ssh-")) {
            keyType.removePrefix("ssh-")
        } else {
            "unknownKeyType"
        }
}