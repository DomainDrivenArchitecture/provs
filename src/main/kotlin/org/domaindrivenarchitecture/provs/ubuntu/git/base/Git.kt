package org.domaindrivenarchitecture.provs.ubuntu.git.base

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.ProvResult
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.*
import org.domaindrivenarchitecture.provs.ubuntu.keys.base.isHostKnown
import org.domaindrivenarchitecture.provs.core.echoCommandForText
import java.io.File

val knownHostsFile = "~/.ssh/known_hosts"


fun Prov.gitClone(repo: String, path: String, pullIfExisting: Boolean = true): ProvResult = def {
    val dir = cmdNoEval("basename $repo .git").out?.trim()

    if (dir == null) {
        return@def ProvResult(false, err = "$repo is not a valid git repository")
    }

    val pathToDir = if (path.endsWith("/")) path + dir else path + "/" + dir
    if (dirExists(pathToDir + "/.git/")) {
        if (pullIfExisting) {
            cmd("cd $pathToDir && git pull")
        } else {
            ProvResult(true, out = "Repo $repo is already existing")
        }
    } else {
        cmd("cd $path && git clone $repo")
    }
}


fun Prov.trustGithub() = def {
    // current fingerprints from https://docs.github.com/en/github/authenticating-to-github/githubs-ssh-key-fingerprints
    val fingerprints = setOf(
        "SHA256:nThbg6kXUpJWGl7E1IGOCspRomTxdCARLviKw6E5SY8 github.com", // (RSA)
        // supported beginning September 14, 2021:
        "SHA256:p2QAMXNIC1TJYWeIOttrVc98/R1BUFWu3/LiyKgUfQM github.com",  // (ECDSA)
        "SHA256:+DiY3wvvV6TuJJhbpZisF/zLDA0zPMSvHdkr4UvCOqU github.com"  // (Ed25519)
    )
    trustHost("github.com", fingerprints)
}


fun Prov.trustGitlab() = def {
    // entries for known_hosts from https://docs.gitlab.com/ee/user/gitlab_com/
    val gitlabFingerprints = """
        gitlab.com ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIAfuCHKVTjquxvt6CM6tdG4SLp1Btn/nOeHHE5UOzRdf
        gitlab.com ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCsj2bNKTBSpIYDEGk9KxsGh3mySTRgMtXL583qmBpzeQ+jqCMRgBqB98u3z++J1sKlXHWfM9dyhSevkMwSbhoR8XIq/U0tCNyokEi/ueaBMCvbcTHhO7FcwzY92WK4Yt0aGROY5qX2UKSeOvuP4D6TPqKF1onrSzH9bx9XUf2lEdWT/ia1NEKjunUqu1xOB/StKDHMoX4/OKyIzuS0q/T1zOATthvasJFoPrAjkohTyaDUz2LN5JoH839hViyEG82yB+MjcFV5MU3N1l1QL3cVUCh93xSaua1N85qivl+siMkPGbO5xR/En4iEY6K2XPASUEMaieWVNTRCtJ4S8H+9
        gitlab.com ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBFSMqzJeV9rUzU4kWitGjeR4PWSa29SPqJ1fVkhtj3Hw9xjLVXVYrU9QlYWrOLXBpQ6KWjbjTDTdDkoohFzgbEY=
    """.trimIndent()
    addTextToFile("\n" + gitlabFingerprints + "\n", File(knownHostsFile))
}


/**
 * Adds ssh keys for specified host (which also can be an ip-address) to ssh-file "known_hosts"
 * Either add the specified rsaFingerprints or - if null - add automatically retrieved keys.
 * Note: adding keys automatically is vulnerable to a man-in-the-middle attack and not considered secure.
 */
// todo: consider making function public and moving to ssh package
private fun Prov.trustHost(host: String, fingerprintsOfKeysToBeAdded: Set<String>?) = def {
    if (isHostKnown(host)) {
        return@def ProvResult(true, out = "Host already known")
    }
    if (!fileExists(knownHostsFile)) {
        createDir(".ssh")
        createFile(knownHostsFile, null)
    }
    if (fingerprintsOfKeysToBeAdded == null) {
        // auto add keys
        cmd("ssh-keyscan $host >> $knownHostsFile")
    } else {
        // logic based on https://serverfault.com/questions/447028/non-interactive-git-clone-ssh-fingerprint-prompt
        val actualKeys = findSshKeys(host)
        if (actualKeys == null || actualKeys.size == 0) {
            return@def ProvResult(false, out = "No valid keys found for host: $host")
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
                return@def ProvResult(
                    false,
                    err = "Fingerprint ($fingerprintToBeAdded) could not be found in actual fingerprints: $actualFingerprints"
                )
            }
            cmd(echoCommandForText(actualKeys.get(indexOfKeyFound) + "\n") + " >> $knownHostsFile")
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
