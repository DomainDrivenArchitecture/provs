package org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base

import org.domaindrivenarchitecture.provs.desktop.domain.KnownHost
import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.*
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.SshKeyPair
import java.io.File


/**
 * Installs ssh keys for active user; ssh filenames depend on the ssh key type, e.g. for public key file: "id_rsa.pub", "id_id_ed25519.pub", etc
 */
fun Prov.configureSshKeys(sshKeys: SshKeyPair) = task {
    createDir(".ssh", "~/")
    createSecretFile("~/.ssh/id_${sshKeys.sshAlgorithmName}.pub", sshKeys.publicKey, "644")
    createSecretFile("~/.ssh/id_${sshKeys.sshAlgorithmName}", sshKeys.privateKey, "600")
}


/**
 * Checks if the specified hostname or Ip is in a known_hosts file
 *
 * @return whether if was found
 */
fun Prov.isKnownHost(hostOrIp: String): Boolean {
    return cmdNoEval("ssh-keygen -F $hostOrIp").out?.isNotEmpty() ?: false
}


/**
 * Adds ssh keys for specified host (which also can be an ip-address) to the ssh-file "known_hosts".
 * If parameter verifyKeys is true the keys are checked against the live keys of the host and only added if valid.
 */
fun Prov.addKnownHost(knownHost: KnownHost, verifyKeys: Boolean = false) = task {
    val knownHostsFile = "~/.ssh/known_hosts"

    if (!checkFile(knownHostsFile)) {
        createDir(".ssh")
        createFile(knownHostsFile, null)
    }
    with(knownHost) {
        for (key in hostKeys) {
            if (!verifyKeys) {
                addTextToFile("\n$hostName $key\n", File(knownHostsFile))
            } else {
                val validKeys = getSshKeys(hostName)
                if (validKeys?.contains(key) == true) {
                    addTextToFile("\n$hostName $key\n", File(knownHostsFile))
                } else {
                    addResultToEval(
                        ProvResult(
                            false,
                            err = "The following key of host [$hostName] could not be verified successfully: " + key
                        )
                    )
                }
            }
        }
    }
}


/**
 * Returns a list of valid ssh keys for the given host (host can also be an ip address), keys are returned as keytype and key BUT WITHOUT the host name
 */
private fun Prov.getSshKeys(host: String, keytype: String? = null): List<String>? {
    val keytypeOption = keytype?.let { " -t $keytype " } ?: ""
    val output = cmd("ssh-keyscan $keytypeOption $host 2>/dev/null").out?.trim()
    return output?.split("\n")?.filter { x -> "" != x }?.map { x -> x.substringAfter(" ") }
}
