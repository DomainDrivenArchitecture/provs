package org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.core.echoCommandForText
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDir
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createSecretFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.KeyPair


const val KNOWN_HOSTS_FILE = "~/.ssh/known_hosts"


/**
 * installs ssh keys for active user
 */
fun Prov.configureSshKeys(sshKeys: KeyPair) = task {
    createDir(".ssh", "~/")
    createSecretFile("~/.ssh/id_rsa.pub", sshKeys.publicKey, "644")
    createSecretFile("~/.ssh/id_rsa", sshKeys.privateKey, "600")
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
 * Either add the specified rsaFingerprints or - if null - add automatically retrieved keys.
 * Note: adding keys automatically is vulnerable to a man-in-the-middle attack, thus considered insecure and not recommended.
 */
fun Prov.trustHost(host: String, fingerprintsOfKeysToBeAdded: Set<String>?) = taskWithResult {
    if (isHostKnown(host)) {
        return@taskWithResult ProvResult(true, out = "Host already known")
    }
    if (!checkFile(KNOWN_HOSTS_FILE)) {
        createDir(".ssh")
        createFile(KNOWN_HOSTS_FILE, null)
    }
    if (fingerprintsOfKeysToBeAdded == null) {
        // auto add keys
        cmd("ssh-keyscan $host >> $KNOWN_HOSTS_FILE")
    } else {
        // logic based on https://serverfault.com/questions/447028/non-interactive-git-clone-ssh-fingerprint-prompt
        val actualKeys = findSshKeys(host)
        if (actualKeys == null || actualKeys.size == 0) {
            return@taskWithResult ProvResult(false, out = "No valid keys found for host: $host")
        }
        val actualFingerprints = getFingerprintsForKeys(actualKeys)
        for (fingerprintToBeAdded in fingerprintsOfKeysToBeAdded) {
            var indexOfKeyFound = -1

            // search for fingerprint in actual fingerprints
            for ((i, actualFingerprint) in actualFingerprints.withIndex()) {
                if (actualFingerprint.contains(fingerprintToBeAdded)) {
                    indexOfKeyFound = i
                    break
                }
            }
            if (indexOfKeyFound == -1) {
                return@taskWithResult ProvResult(
                    false,
                    err = "Fingerprint ($fingerprintToBeAdded) could not be found in actual fingerprints: $actualFingerprints"
                )
            }
            cmd(echoCommandForText(actualKeys.get(indexOfKeyFound) + "\n") + " >> $KNOWN_HOSTS_FILE")
        }
        ProvResult(true)
    }
}


/**
 * Returns a list of valid ssh keys for the given host (host can also be an ip address)
 */
private fun Prov.findSshKeys(host: String): List<String>? {
    return cmd("ssh-keyscan $host 2>/dev/null").out?.split("\n")?.filter { x -> "" != x }
}


/**
 * Returns a list of fingerprints of the given sshKeys; the returning list has same size and order as the specified list of sshKeys
 */
private fun Prov.getFingerprintsForKeys(sshKeys: List<String>): List<String> {
    return sshKeys.map { x -> cmd("echo \"$x\" | ssh-keygen -lf -").out ?: "" }
}
