package org.domaindrivenarchitecture.provs.framework.ubuntu.git.base

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.*
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base.KNOWN_HOSTS_FILE
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base.trustHost
import java.io.File


/**
 * Clones a git repository in the specified targetPath or tries git pull if repo already existing and parameter pullIfExisting is true.
 * If specified, the targetFolderName is used as basename for the repo, otherwise the basename (directory) is retrieved from repoSource
 */
fun Prov.gitClone(
    repoSource: String,
    targetPath: String = "",
    pullIfExisting: Boolean = true,
    targetFolderName: String? = null
): ProvResult = taskWithResult {
    // if specified, use targetFolderName as basename or otherwise retrieve basename from repoSource
    val basename = targetFolderName ?: cmdNoEval("basename $repoSource .git").out?.trim()
    // return err if basename could not be retrieved from repoSource
    ?: return@taskWithResult ProvResult(false, err = "$repoSource is not a valid git repository source path.")

    val pathWithBasename = targetPath.normalizePath() + basename

    // check if repo is already on target machine
    if (checkDir(pathWithBasename + "/.git/")) {
        if (pullIfExisting) {
            cmd("cd $pathWithBasename && git pull")
        } else {
            ProvResult(true, out = "Repo [$pathWithBasename] already exists, but might not be up-to-date.")
        }
    } else {
        // create targetPath (if not yet existing)
        if (!checkDir(targetPath)) {
            createDirs(targetPath)
        }
        cmd("cd $targetPath && git clone $repoSource ${targetFolderName ?: ""}")
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

