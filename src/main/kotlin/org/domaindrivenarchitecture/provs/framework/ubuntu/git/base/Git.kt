package org.domaindrivenarchitecture.provs.framework.ubuntu.git.base

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.*
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base.KNOWN_HOSTS_FILE
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base.trustHost
import java.io.File


fun Prov.gitClone(repo: String, path: String, pullIfExisting: Boolean = true): ProvResult = task {
    val dir = cmdNoEval("basename $repo .git").out?.trim()

    if (dir == null) {
        return@task ProvResult(false, err = "$repo is not a valid git repository")
    }

    val pathToDir = if (path.endsWith("/")) path + dir else path + "/" + dir
    if (checkDir(pathToDir + "/.git/")) {
        if (pullIfExisting) {
            cmd("cd $pathToDir && git pull")
        } else {
            ProvResult(true, out = "Repo $repo is already existing")
        }
    } else {
        cmd("cd $path && git clone $repo")
    }
}


fun Prov.trustGithub() = task {
    // current fingerprints from https://docs.github.com/en/github/authenticating-to-github/githubs-ssh-key-fingerprints
    val fingerprints = setOf(
        "SHA256:nThbg6kXUpJWGl7E1IGOCspRomTxdCARLviKw6E5SY8 github.com", // (RSA)
        // supported beginning September 14, 2021:
        "SHA256:p2QAMXNIC1TJYWeIOttrVc98/R1BUFWu3/LiyKgUfQM github.com",  // (ECDSA)
        "SHA256:+DiY3wvvV6TuJJhbpZisF/zLDA0zPMSvHdkr4UvCOqU github.com"  // (Ed25519)
    )
    trustHost("github.com", fingerprints)
}


fun Prov.trustGitlab() = task {
    // entries for known_hosts from https://docs.gitlab.com/ee/user/gitlab_com/
    val gitlabFingerprints = """
        gitlab.com ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIAfuCHKVTjquxvt6CM6tdG4SLp1Btn/nOeHHE5UOzRdf
        gitlab.com ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCsj2bNKTBSpIYDEGk9KxsGh3mySTRgMtXL583qmBpzeQ+jqCMRgBqB98u3z++J1sKlXHWfM9dyhSevkMwSbhoR8XIq/U0tCNyokEi/ueaBMCvbcTHhO7FcwzY92WK4Yt0aGROY5qX2UKSeOvuP4D6TPqKF1onrSzH9bx9XUf2lEdWT/ia1NEKjunUqu1xOB/StKDHMoX4/OKyIzuS0q/T1zOATthvasJFoPrAjkohTyaDUz2LN5JoH839hViyEG82yB+MjcFV5MU3N1l1QL3cVUCh93xSaua1N85qivl+siMkPGbO5xR/En4iEY6K2XPASUEMaieWVNTRCtJ4S8H+9
        gitlab.com ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBFSMqzJeV9rUzU4kWitGjeR4PWSa29SPqJ1fVkhtj3Hw9xjLVXVYrU9QlYWrOLXBpQ6KWjbjTDTdDkoohFzgbEY=
    """.trimIndent()
    addTextToFile("\n" + gitlabFingerprints + "\n", File(KNOWN_HOSTS_FILE))
}

