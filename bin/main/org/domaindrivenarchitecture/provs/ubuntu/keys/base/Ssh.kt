package org.domaindrivenarchitecture.provs.ubuntu.keys.base

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.ProvResult
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.createDir
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.createSecretFile
import org.domaindrivenarchitecture.provs.ubuntu.keys.KeyPair


/**
 * installs ssh keys for active user
 */
fun Prov.configureSshKeys(sshKeys: KeyPair) = def {
    createDir(".ssh", "~/")
    createSecretFile("~/.ssh/id_rsa.pub", sshKeys.publicKey, "644")
    createSecretFile("~/.ssh/id_rsa", sshKeys.privateKey, "600")
    configureSSHClient()
}

fun Prov.configureSSHClient() = def {
    // TODO("Not yet implemented")
    ProvResult(true)
}


/**
 * Specifies a host or Ip to be trusted
 *
 * ATTENTION:
 * This method is NOT secure as a man-in-the-middle could compromise the connection.
 * Don't use this for critical systems resp. environments
 */
fun Prov.trustServer(hostOrIp: String) = def {
    cmd("ssh-keyscan $hostOrIp >> ~/.ssh/known_hosts")
}


/**
 * Checks if the specified hostname or Ip is in a known_hosts file
 *
 * @return whether if was found
 */
fun Prov.isHostKnown(hostOrIp: String) : Boolean {
    return cmdNoEval("ssh-keygen -F $hostOrIp").out?.isNotEmpty() ?: false
}

