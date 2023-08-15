package org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.*
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.SshKeyPair
import java.io.File


const val KNOWN_HOSTS_FILE = "~/.ssh/known_hosts"

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
fun Prov.isHostKnown(hostOrIp: String) : Boolean {
    return cmdNoEval("ssh-keygen -F $hostOrIp").out?.isNotEmpty() ?: false
}


/**
 * Adds ssh keys for specified host (which also can be an ip-address) to ssh-file "known_hosts"
 * Either add the specified keys or - if null - add keys automatically retrieved.
 * Note: adding keys automatically is vulnerable to a man-in-the-middle attack, thus considered insecure and not recommended.
 */
fun Prov.addKnownHost(host: String, keysToBeAdded: List<String>?, verifyKeys: Boolean = false) = task {
    if (!checkFile(KNOWN_HOSTS_FILE)) {
        createDir(".ssh")
        createFile(KNOWN_HOSTS_FILE, null)
    }
    if (keysToBeAdded == null) {
        // auto add keys
        cmd("ssh-keyscan $host >> $KNOWN_HOSTS_FILE")
    } else {
        for (key in keysToBeAdded) {
            if (!verifyKeys) {
                addTextToFile("\n$host $key\n", File(KNOWN_HOSTS_FILE))
            } else {
                val validKeys = getSshKeys(host)
                if (validKeys?.contains(key) == true) {
                    addTextToFile("\n$host $key\n", File(KNOWN_HOSTS_FILE))
                } else {
                    addResultToEval(ProvResult(false, err = "The following key of host [$host] could not be verified successfully: " + key))
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
