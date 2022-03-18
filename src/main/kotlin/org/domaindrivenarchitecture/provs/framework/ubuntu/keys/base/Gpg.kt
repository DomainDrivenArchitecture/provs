package org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDir
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createSecretFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkDir
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.KeyPair
import org.domaindrivenarchitecture.provs.framework.core.echoCommandForText


/**
 * Installs a gpg key pair for the current user.
 *
 * @param gpgKeys
 * @param trust whether to trust keys with trust-level 5 (ultimate)
 */
fun Prov.configureGpgKeys(gpgKeys: KeyPair, trust: Boolean = false, skipIfExistin: Boolean = true) = task {
    aptInstall("gnupg")
    val fingerprint = gpgFingerprint(gpgKeys.publicKey.plain())
    if (fingerprint == null) {
        ProvResult(false, err = "Fingerprint of key could not be determined")
    } else {
        if (gpgKeysInstalled(fingerprint) && skipIfExistin) {
            ProvResult(true, out = "Keys were already installed")
        } else {
            val pubkeyFile = "~/pub-key.asc"
            val privkeyFile = "~/priv-key.asc"

            createSecretFile(pubkeyFile, gpgKeys.publicKey)
            createSecretFile(privkeyFile, gpgKeys.privateKey)

            cmd("gpg --import $pubkeyFile")

            // using option --batch for older keys; see https://superuser.com/questions/1135812/gpg2-asking-for-passphrase-when-importing-secret-keys
            cmd("gpg --batch --import $privkeyFile")

            if (trust) {
                cmd("printf \"5\\ny\\n\" | gpg --no-tty --command-fd 0 --expert --edit-key $fingerprint trust")
            }

            cmd("shred $pubkeyFile")
            cmd("shred $privkeyFile")

            configureGPGAgent()
        }
    }
}


private fun Prov.configureGPGAgent() = task {
    if (checkDir(".gnupg")) {
        createDir(".gnupg", "~/")
    }
    val content = """
        allow-preset-passphrase
        allow-loopback-pinentry
    """.trimIndent()
    createFile("~/.gnupg/gpg-agent.conf", content)
}


private fun Prov.gpgKeysInstalled(fingerprint: String): Boolean  {
    return cmdNoLog("gpg --list-keys $fingerprint").success
}


fun Prov.gpgFingerprint(pubKey: String): String? {
    val result =
        cmdNoLog(" " + echoCommandForText(pubKey) + " | gpg --with-colons --import-options show-only --import --fingerprint")
    return result.out?.let { """^fpr:*([A-Z0-9]*):$""".toRegex(RegexOption.MULTILINE).find(it)?.groupValues?.get(1) }
}
