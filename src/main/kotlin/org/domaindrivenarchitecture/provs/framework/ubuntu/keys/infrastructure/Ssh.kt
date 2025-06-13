package org.domaindrivenarchitecture.provs.framework.ubuntu.keys.infrastructure

import org.domaindrivenarchitecture.provs.desktop.domain.KnownHost
import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.local
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.*
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.domain.SshKeyPair
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
 * Checks if the specified host (domain name or IP) and (optional) port is contained in the known_hosts file
 */
fun Prov.isKnownHost(hostOrIp: String, port: Int? = null): Boolean {
    val hostWithPotentialPort = port?.let { formatHostForKnownHostsFile(hostOrIp, port) } ?: hostOrIp
    return cmdNoEval("ssh-keygen -F $hostWithPotentialPort").out?.isNotEmpty() ?: false
}

fun formatHostForKnownHostsFile(hostOrIp: String, port: Int? = null): String {
    return port?.let { "[$hostOrIp]:$port" } ?: hostOrIp
}


/**
 * Adds ssh keys for specified host (which also can be an ip-address) to the ssh-file "known_hosts".
 * If parameter verifyKeys is true, the keys are checked against the live keys of the host and added only if valid.
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
                addTextToFile("\n${formatHostForKnownHostsFile(hostName, port)} $key\n", File(knownHostsFile))
            } else {
                val validKeys = findSshKeys(hostName, port)
                if (validKeys?.contains(key) == true) {
                    val formattedHost = formatHostForKnownHostsFile(hostName, port)
                    addTextToFile("\n$formattedHost $key\n", File(knownHostsFile))
                } else {
                    addResult(
                        false,
                        err = "The following key of host [$hostName] could not be verified successfully: " + key
                    )
                }
            }
        }
    }
}


/**
 * Returns a list of valid ssh keys for the given host (host can also be an ip address),
 * keys are returned (space-separated) as keytype and key, but WITHOUT the host name.*
 * If no port is specified, the keys for the default port (22) are returned.
 * If no keytype is specified, keys are returned for all keytypes.
 */
fun Prov.findSshKeys(host: String, port: Int? = null, keytype: String? = null): List<String>? {
    val portOption = port?.let { " -p $port " } ?: ""
    val keytypeOption = keytype?.let { " -t $keytype " } ?: ""
    val output = cmd("ssh-keyscan $portOption $keytypeOption $host 2>/dev/null").out?.trim()
    return output?.split("\n")?.filter { x -> "" != x }?.map { x -> x.substringAfter(" ") }
}

fun main() {
    val k = local().findSshKeys("repo.prod.meissa.de", 2222)
    println(k)
}