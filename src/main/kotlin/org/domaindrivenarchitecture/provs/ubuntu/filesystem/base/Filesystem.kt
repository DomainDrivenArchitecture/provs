package org.domaindrivenarchitecture.provs.ubuntu.filesystem.base

import org.domaindrivenarchitecture.provs.core.*
import org.domaindrivenarchitecture.provs.core.platforms.SHELL
import java.io.File


fun Prov.fileExists(file: String, sudo: Boolean = false): Boolean {
    return cmdNoEval((if (sudo) "sudo " else "") + "test -e " + file).success
}


fun Prov.createFile(
    fullyQualifiedFilename: String,
    text: String?,
    posixFilePermission: String? = null,
    sudo: Boolean = false
): ProvResult =
    def {
        val withSudo = if (sudo) "sudo " else ""
        posixFilePermission?.let {
            ensureValidPosixFilePermission(posixFilePermission)
            cmd(withSudo + "install -m $posixFilePermission /dev/null $fullyQualifiedFilename")
        }
        if (text != null) {
            if (sudo) {
                cmd(
                    "printf " + text.escapeProcentForPrintf()
                        .escapeAndEncloseByDoubleQuoteForShell() + " | sudo tee $fullyQualifiedFilename > /dev/null"
                )
            } else {
                cmd(
                    "printf " + text.escapeProcentForPrintf()
                        .escapeAndEncloseByDoubleQuoteForShell() + " > $fullyQualifiedFilename"
                )
            }
        } else {
            cmd(withSudo + "touch $fullyQualifiedFilename")
        }
    }


fun Prov.createSecretFile(
    fullyQualifiedFilename: String,
    secret: Secret,
    posixFilePermission: String? = null
): ProvResult =
    def {
        posixFilePermission?.let {
            ensureValidPosixFilePermission(posixFilePermission)
            cmd("install -m $posixFilePermission /dev/null $fullyQualifiedFilename")
        }
        cmdNoLog("echo '" + secret.plain().escapeSingleQuote() + "' > $fullyQualifiedFilename")
    }


fun Prov.deleteFile(file: String, path: String? = null, sudo: Boolean = false): ProvResult = def {
    cmd((path?.let { "cd $path && " } ?: "") + (if (sudo) "sudo " else "") + "rm $file")
}


fun Prov.fileContainsText(file: String, content: String, sudo: Boolean = false): Boolean {
    return cmdNoEval((if (sudo) "sudo " else "") + "grep '${content.escapeSingleQuote()}' $file").success
}


fun Prov.fileContent(file: String, sudo: Boolean = false): String? {
    return cmd((if (sudo) "sudo " else "") + "cat $file").out
}


fun Prov.addTextToFile(
    text: String,
    file: File,
    doNotAddIfExisting: Boolean = true,
    sudo: Boolean = false
): ProvResult =
    def {
        // TODO find solution without trim handling spaces, newlines, etc correctly
        val findCmd = "grep '${text.trim().escapeSingleQuote()}' ${file}"
        val findResult = cmdNoEval(if (sudo) findCmd.sudoizeCommand() else findCmd)
        if (!findResult.success || !doNotAddIfExisting) {
            val addCmd = "printf \"" + text.escapeDoubleQuote() + "\" >> " + file
            cmd(if (sudo) addCmd.sudoizeCommand() else addCmd)
        } else {
            ProvResult(true)
        }
    }


fun Prov.replaceTextInFile(file: String, oldText: String, replacement: String) = def {
    replaceTextInFile(file, Regex.fromLiteral(oldText), Regex.escapeReplacement(replacement))
}


fun Prov.replaceTextInFile(file: String, oldText: Regex, replacement: String) = def {
    // todo: only use sudo for root or if owner different from current
    val content = fileContent(file, true)
    if (content != null) {
        cmd("sudo truncate -s 0 $file")
        addTextToFile(content.replace(oldText, Regex.escapeReplacement(replacement)), File(file), sudo = true)
    } else {
        ProvResult(false)
    }
}


fun Prov.insertTextInFile(file: String, textBehindWhichToInsert: Regex, textToInsert: String) = def {
    // todo: only use sudo for root or if owner different from current
    val content = fileContent(file, true)
    if (content != null) {
        val match = textBehindWhichToInsert.find(content)
        if (match != null) {
            cmd("sudo truncate -s 0 $file")
            addTextToFile(
                content.replace(textBehindWhichToInsert, match.value + Regex.escapeReplacement(textToInsert)),
                File(file),
                sudo = true
            )
        } else {
            ProvResult(false, err = "Text not found")
        }
    } else {
        ProvResult(false)
    }
}


fun Prov.dirExists(dir: String, path: String? = null, sudo: Boolean = false): Boolean {
    val effectivePath = if (path != null) path else
        (if (dir.startsWith(File.separator)) File.separator else "~" + File.separator)
    val cmd = "cd $effectivePath && test -d $dir"
    return cmdNoEval(if (sudo) cmd.sudoizeCommand() else cmd).success
}


fun Prov.createDir(
    dir: String,
    path: String = "~/",
    failIfExisting: Boolean = false,
    sudo: Boolean = false
): ProvResult = def {
    if (!failIfExisting && dirExists(dir, path, sudo)) {
        ProvResult(true)
    } else {
        val cmd = "cd $path && mkdir $dir"
        cmd(if (sudo) cmd.sudoizeCommand() else cmd)
    }
}


fun Prov.createDirs(
    dirs: String,
    path: String = "~/",
    failIfExisting: Boolean = false,
    sudo: Boolean = false
): ProvResult = def {
    if (!failIfExisting && dirExists(dirs, path, sudo)) {
        ProvResult(true)
    } else {
        val cmd = "cd $path && mkdir -p $dirs"
        cmd(if (sudo) cmd.sudoizeCommand() else cmd)
    }
}


fun Prov.deleteDir(dir: String, path: String, sudo: Boolean = false): ProvResult {
    if ("" == path)
        throw RuntimeException("In deleteDir: path must not be empty.")
    val cmd = "cd $path && rmdir $dir"
    return if (!sudo) {
        cmd(cmd)
    } else {
        cmd(cmd.sudoizeCommand())
    }
}


fun Prov.userHome(): String {
    val user = cmd("whoami").out
    if (user == null) {
        throw RuntimeException("Could not determine user with whoami")
    } else {
        // assume default home folder
        return "/home/" + user.trim() + "/"
    }
}


private fun ensureValidPosixFilePermission(posixFilePermission: String) {
    if (!Regex("^[0-7]{3}$").matches(posixFilePermission)) throw RuntimeException("Wrong file permission ($posixFilePermission), permission must consist of 3 digits as e.g. 664 ")
}


private fun String.sudoizeCommand(): String {
    return "sudo " + SHELL + " -c " + this.escapeAndEncloseByDoubleQuoteForShell()
}