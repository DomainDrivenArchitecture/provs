package org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base

import org.domaindrivenarchitecture.provs.framework.core.platforms.SHELL
import org.domaindrivenarchitecture.provs.framework.core.*
import org.domaindrivenarchitecture.provs.framework.core.getLocalFileContent
import java.io.File
import java.util.*


/**
 * Returns true if the given file exists.
 */
@Deprecated("Use checkFile", replaceWith = ReplaceWith("checkFile(file)"))
fun Prov.fileExists(file: String, sudo: Boolean = false): Boolean {
    return cmdNoEval(prefixWithSudo("test -e " + file, sudo)).success
}

/**
 * Returns true if the given file exists.
 */
fun Prov.checkFile(file: String, sudo: Boolean = false): Boolean {
    return cmdNoEval(prefixWithSudo("test -e " + file, sudo)).success
}


/**
 * Creates a file with its content retrieved from a local resource file
 */
fun Prov.createFileFromResource(
    fullyQualifiedFilename: String,
    resourceFilename: String,
    resourcePath: String = "",
    posixFilePermission: String? = null,
    sudo: Boolean = false
): ProvResult = task {
    createFile(
        fullyQualifiedFilename,
        getResourceAsText(resourcePath.endingWithFileSeparator() + resourceFilename),
        posixFilePermission,
        sudo
    )
}


/**
 * Creates a file with its content retrieved of a local resource file in which placeholders are substituted with the specified values.
 */
fun Prov.createFileFromResourceTemplate(
    fullyQualifiedFilename: String,
    resourceFilename: String,
    resourcePath: String = "",
    values: Map<String, String>,
    posixFilePermission: String? = null,
    sudo: Boolean = false
): ProvResult = task {
    createFile(
        fullyQualifiedFilename,
        getResourceAsText(resourcePath.endingWithFileSeparator() + resourceFilename).resolve(values),
        posixFilePermission,
        sudo
    )
}


/**
 * Copies a file from the local environment to the running Prov instance.
 * In case the running ProvInstance is also local, it would copy from local to local.
 */
fun Prov.copyFileFromLocal(
    fullyQualifiedFilename: String,
    fullyQualifiedLocalFilename: String,
    posixFilePermission: String? = null,
    sudo: Boolean = false
): ProvResult = task {
    createFile(
        fullyQualifiedFilename,
        getLocalFileContent(fullyQualifiedLocalFilename),
        posixFilePermission,
        sudo
    )
}


/**
 * Creates a file with the specified data. If text is null, an empty file is created
 */
fun Prov.createFile(
    fullyQualifiedFilename: String,
    text: String?,
    posixFilePermission: String? = null,
    sudo: Boolean = false,
    overwriteIfExisting: Boolean = true
): ProvResult = task {
    val maxBlockSize = 50000
    val withSudo = if (sudo) "sudo " else ""

    posixFilePermission?.let {
        ensureValidPosixFilePermission(posixFilePermission)
    }
    if (!overwriteIfExisting && checkFile(fullyQualifiedFilename, sudo)) {
        return@task ProvResult(true, "File $fullyQualifiedFilename already existing.")
    }

    val modeOption = posixFilePermission?.let { "-m $it" } ?: ""

    // create empty file resp. clear file
    cmd(withSudo + "install $modeOption /dev/null $fullyQualifiedFilename")

    if (text != null) {
        val chunkedTest = text.chunked(maxBlockSize)
        for (chunk in chunkedTest) {
            // todo: consider usage of function addTextToFile
            cmdNoLog(
                "printf '%s' " + chunk
                    .escapeAndEncloseByDoubleQuoteForShell() + " | $withSudo tee -a $fullyQualifiedFilename > /dev/null"
            )
        }
        ProvResult(true) // dummy
    } else {
        cmd(withSudo + "touch $fullyQualifiedFilename")
    }
}


fun Prov.createSecretFile(
    fullyQualifiedFilename: String,
    secret: Secret,
    posixFilePermission: String? = null
): ProvResult = task {
    posixFilePermission?.let {
        ensureValidPosixFilePermission(posixFilePermission)
        cmd("install -m $posixFilePermission /dev/null $fullyQualifiedFilename")
    }
    cmdNoLog("echo '" + secret.plain().escapeSingleQuote() + "' > $fullyQualifiedFilename")
}


fun Prov.deleteFile(file: String, path: String? = null, sudo: Boolean = false): ProvResult = task {
    val fullyQualifiedFilename = (path?.normalizePath() ?: "") + file
    if (checkFile(fullyQualifiedFilename, sudo = sudo)) {
        cmd(prefixWithSudo("rm $fullyQualifiedFilename", sudo))
    } else {
        ProvResult(true, "File to be deleted did not exist.")
    }
}


fun Prov.fileContainsText(file: String, content: String, sudo: Boolean = false): Boolean {
    if (!checkFile(file, sudo)) {
        return false
    }

    // use grep for a single line or for a single line enclosed by a newline
    if (!content.contains("\n") || (content.length >= 3 && !content.drop(1).dropLast(1).contains("\n"))) {
        return cmdNoEval(prefixWithSudo("grep -- '${content.escapeSingleQuote().trim('\n')}' $file", sudo)).success
    } else {
        val fileContent = fileContent(file, sudo = sudo)
        return fileContent?.contains(content) ?: false
    }
}


fun Prov.fileContent(file: String, sudo: Boolean = false): String? {
    val largeFileSize = 40000

    val size = fileSize(file, sudo)
    if (size == null || size > largeFileSize) {
        return fileContentLargeFile(file, sudo)
    } else {
        return cmdNoEval(prefixWithSudo("cat $file", sudo)).out
    }
}

fun Prov.fileContentLargeFile(file: String, sudo: Boolean = false, chunkSize: Int = 10000): String? {
    require(chunkSize <= 40000) { "Chunk size must be < 40000" }
    val maxSizeLargeFileContent = 10000000  // 10 MB
    val size = fileSize(file, sudo)
    if (size != null && size > maxSizeLargeFileContent) {
        throw IllegalArgumentException("Cannot retrieve file content of files larger than: $maxSizeLargeFileContent bytes")
    }

    var offset = 0

    var resultString: String? = null
    do {
        // todo : file paths starting with ~/ are not yet supported
        val chunkResult =
            cmdNoEval(prefixWithSudo("dd if=\"$file\" iflag=skip_bytes,count_bytes,fullblock bs=\"$chunkSize\" skip=\"$offset\" count=\"$chunkSize\" status=none | base64", sudo))

        // check first chunk
        if (resultString == null) {
            if (!chunkResult.success) {
                return resultString
            } else {
                resultString = ""
            }
        }

        val b = chunkResult.out?.trim() ?: ""
        offset += chunkSize

        if (b.isEmpty() || b == "0") {
            break
        }

        // Use MimeDecoder to ignore newlines (\n)
        val decodedBytes: ByteArray = Base64.getMimeDecoder().decode( b )
        val dec = String(decodedBytes)
        resultString += dec
    } while (true)

    return resultString
}


fun Prov.addTextToFile(
    text: String,
    file: File,
    doNotAddIfExisting: Boolean = true,
    sudo: Boolean = false
): ProvResult =
    task {
        val fileContainsText = fileContainsText(file.path, text, sudo = sudo)
        if (fileContainsText && doNotAddIfExisting) {
            return@task ProvResult(true, out = "Text already in file")
        }
        cmd(
            "printf '%s' " + text
                .escapeAndEncloseByDoubleQuoteForShell() + " | ${sudoAsText(sudo)} tee -a ${file.path} > /dev/null"
        )
    }


fun Prov.replaceTextInFile(file: String, oldText: String, replacement: String) = task {
    replaceTextInFile(file, Regex.fromLiteral(oldText), Regex.escapeReplacement(replacement))
}


fun Prov.replaceTextInFile(file: String, oldText: Regex, replacement: String) = task {
    // todo: only use sudo for root or if owner different from current
    val content = fileContent(file, true)
    if (content != null) {
        cmd("sudo truncate -s 0 $file")
        addTextToFile(content.replace(oldText, Regex.escapeReplacement(replacement)), File(file), sudo = true)
    } else {
        ProvResult(false)
    }
}


fun Prov.insertTextInFile(file: String, textBehindWhichToInsert: Regex, textToInsert: String) = task {
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


// =============================  folder operations  ==========================

fun Prov.checkDir(dir: String, path: String? = null, sudo: Boolean = false): Boolean {
    val effectivePath = if (path != null) path else
        (if (dir.startsWith(File.separator)) File.separator else "~" + File.separator)
    val cmd = "cd $effectivePath && test -d $dir"
    return cmdNoEval(if (sudo) cmd.sudoizeCommand() else cmd).success
}

@Deprecated("Use checkDir instead.", replaceWith = ReplaceWith("checkDir(dir)"))
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
): ProvResult = task {
    if (!failIfExisting && checkDir(dir, path, sudo)) {
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
): ProvResult = task {
    if (!failIfExisting && checkDir(dirs, path, sudo)) {
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


// --------------------- various functions ----------------------
fun Prov.userHome(): String {
    val user = cmd("whoami").out?.trim()
    if (user == null) {
        throw RuntimeException("Could not determine user with whoami")
    } else {
        // set default home folder
        return if (user == "root")
            "/root/"
        else
            "/home/$user/"
    }
}


/**
 * Returns number of bytes of a file or null if size could not be determined
 */
fun Prov.fileSize(filename: String, sudo: Boolean = false): Int? {
    val result = cmdNoEval("wc -c < $filename", sudo = sudo)
    return result.out?.trim()?.toIntOrNull()
}


private fun ensureValidPosixFilePermission(posixFilePermission: String) {
    if (!Regex("^[0-7]{3}$").matches(posixFilePermission)) throw IllegalArgumentException("Wrong file permission ($posixFilePermission), permission must consist of 3 digits as e.g. 664")
}


/**
 * Returns a command encapsulated in a shell command and executed with sudo.
 * For simple cases consider sudo as prefix instead.
 * @see prefixWithSudo
 */
private fun String.sudoizeCommand(): String {
    return "sudo " + SHELL + " -c " + this.escapeAndEncloseByDoubleQuoteForShell()
}


/**
 * Returns path with a trailing fileSeparator if path not empty
 */
fun String.normalizePath(): String {
    return if (this == "" || this.endsWith(fileSeparatorChar())) this else this + fileSeparator()
}