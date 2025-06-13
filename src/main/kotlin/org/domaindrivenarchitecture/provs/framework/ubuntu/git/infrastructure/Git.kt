package org.domaindrivenarchitecture.provs.framework.ubuntu.git.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.*


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
        // create targetPath if not yet existing
        if (!checkDir(targetPath)) {
            createDirs(targetPath)
        }

        // Note that all output of git clone on Linux is shown in stderr (normal progress info AND errors),
        // which might be confusing in the logfile.
        cmd("cd $targetPath && git clone $repoSource ${targetFolderName ?: ""}")
    }
}

