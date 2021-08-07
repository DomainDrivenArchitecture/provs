package io.provs.ubuntu.git.base

import io.provs.core.Prov
import io.provs.core.ProvResult
import io.provs.ubuntu.filesystem.base.addTextToFile
import io.provs.ubuntu.filesystem.base.createDir
import io.provs.ubuntu.filesystem.base.dirExists
import io.provs.ubuntu.keys.base.isHostKnown
import io.provs.ubuntu.utils.printToShell
import java.io.File


/**
 * @param host name or ip
 * @param rsaFingerprints
 */
private fun Prov.trustHost(host: String, rsaFingerprints: Set<String>) = def {
    if (!isHostKnown(host)) {
        // logic based on https://serverfault.com/questions/447028/non-interactive-git-clone-ssh-fingerprint-prompt
        val key = cmd("ssh-keyscan $host").out
        if (key == null) {
            ProvResult(false, "No key retrieved for $host")
        } else {
            val c = printToShell(key).trim()
            val fpr = cmd(c + " | ssh-keygen -lf -").out
            if (rsaFingerprints.contains(fpr)
            ) {
                createDir(".ssh", "~/")
                cmd(printToShell(key) + " >> ~/.ssh/known_hosts")
            } else {
                ProvResult(false, "Fingerprint $fpr not valid for $host")
            }
        }
    } else {
        ProvResult(true)
    }
}


fun Prov.gitClone(repo: String, path: String, pullIfExisting: Boolean = true): ProvResult = def {
    val dir = cmdNoEval("basename $repo .git").out?.trim()
    if (dir == null) {
        ProvResult(false, err = "$repo is not a valid git repository")
    } else {
        val pathToDir = if (path.endsWith("/")) path + dir else path + "/" + dir
        if (dirExists(pathToDir + "/.git/")) {
            if (pullIfExisting) {
                cmd("cd $pathToDir && git pull")
            } else {
                ProvResult(true, out = "Repo $repo is already existing")
            }
        }  else {
            cmd("cd $path && git clone $repo")
        }
    }
}


fun Prov.trustGithub() = def {
    // current see https://docs.github.com/en/github/authenticating-to-github/githubs-ssh-key-fingerprints
    val fingerprints = setOf(
        "2048 SHA256:nThbg6kXUpJWGl7E1IGOCspRomTxdCARLviKw6E5SY8 github.com (RSA)\n",
        "2048 SHA256:br9IjFspm1vxR3iA35FWE+4VTyz1hYVLIE2t1/CeyWQ github.com (RSA)\n"
    )

    trustHost("github.com", fingerprints)
}


fun Prov.trustGitlab() = def {
    // from https://docs.gitlab.com/ee/user/gitlab_com/
    val gitlabFingerprints = """
        gitlab.com ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIAfuCHKVTjquxvt6CM6tdG4SLp1Btn/nOeHHE5UOzRdf
        gitlab.com ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCsj2bNKTBSpIYDEGk9KxsGh3mySTRgMtXL583qmBpzeQ+jqCMRgBqB98u3z++J1sKlXHWfM9dyhSevkMwSbhoR8XIq/U0tCNyokEi/ueaBMCvbcTHhO7FcwzY92WK4Yt0aGROY5qX2UKSeOvuP4D6TPqKF1onrSzH9bx9XUf2lEdWT/ia1NEKjunUqu1xOB/StKDHMoX4/OKyIzuS0q/T1zOATthvasJFoPrAjkohTyaDUz2LN5JoH839hViyEG82yB+MjcFV5MU3N1l1QL3cVUCh93xSaua1N85qivl+siMkPGbO5xR/En4iEY6K2XPASUEMaieWVNTRCtJ4S8H+9
        gitlab.com ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBFSMqzJeV9rUzU4kWitGjeR4PWSa29SPqJ1fVkhtj3Hw9xjLVXVYrU9QlYWrOLXBpQ6KWjbjTDTdDkoohFzgbEY=
    """.trimIndent()
    addTextToFile("\n" + gitlabFingerprints+ "\n", File("~/.ssh/known_hosts"))
}


