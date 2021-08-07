package io.provs.ubuntu.keys.base

import io.provs.Prov
import io.provs.ProvResult
import io.provs.ubuntu.filesystem.base.createDir
import io.provs.ubuntu.filesystem.base.createFile
import io.provs.ubuntu.filesystem.base.createSecretFile
import io.provs.ubuntu.filesystem.base.dirExists
import io.provs.ubuntu.install.base.aptInstall
import io.provs.ubuntu.keys.KeyPair
import io.provs.ubuntu.utils.printToShell


/**
 * Installs a gpg key pair for the current user.
 *
 * @param gpgKeys
 * @param trust whether to trust keys with trust-level 5 (ultimate)
 */
fun Prov.configureGpgKeys(gpgKeys: KeyPair, trust: Boolean = false, skipIfExistin: Boolean = true) = requireAll {
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


private fun Prov.configureGPGAgent() = def {
    if (dirExists(".gnupg")) {
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
        cmdNoLog(" " + printToShell(pubKey) + " | gpg --with-colons --import-options show-only --import --fingerprint")
    return result.out?.let { """^fpr:*([A-Z0-9]*):$""".toRegex(RegexOption.MULTILINE).find(it)?.groupValues?.get(1) }
}
