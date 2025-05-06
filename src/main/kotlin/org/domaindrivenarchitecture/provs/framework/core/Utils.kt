package org.domaindrivenarchitecture.provs.framework.core

import org.domaindrivenarchitecture.provs.framework.core.docker.provideContainer
import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.framework.core.processors.ContainerUbuntuHostProcessor
import org.domaindrivenarchitecture.provs.framework.core.processors.RemoteProcessor
import org.domaindrivenarchitecture.provs.framework.core.tags.Api
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.fileContent
import java.io.File
import java.net.InetAddress


/**
 * Returns the name of the calling function but excluding some functions of the prov framework
 * in order to return the "real" calling function.
 * Note: names of inner functions (i.e. which are defined inside other functions) are not
 * supported in the sense that always the name of the outer function is returned instead.
 */
internal fun getCallingMethodName(): String? {
    val offsetVal = 1
    val exclude = arrayOf("task", "task\$default", "taskWithResult\$default", "taskWithResult", "def", "def\$default", "record", "invoke", "invoke0", "evaluate", "evaluate\$default", )
    // suffixes are also ignored as method names but will be added as suffix in the evaluation results
    val suffixes = arrayOf("optional", "optional\$default", "requireAll", "requireLast", "requireLast\$default", "inContainer")

    var suffix = ""
    val callingFrame = Thread.currentThread().stackTrace
    for (i in 0 until (callingFrame.size - 1)) {
        if (callingFrame[i].methodName == "getCallingMethodName") {
            var method = callingFrame[i + offsetVal].methodName
            var inc = 0
            while ((method in exclude) or (method in suffixes)) {
                if (method in suffixes && suffix == "") {
                    suffix = method.split("$")[0]
                }
                inc++
                method = callingFrame[i + offsetVal + inc].methodName
            }
            // substring before $ - as some methods (e.g. in tests) can have names like "myMethod$lambda$33"
            return method.substringBefore("$") + if (suffix.isBlank()) "" else " ($suffix)"
        }
    }
    return null
}


// ---------------------------  String extensions  ----------------------------
fun String.escapeNewline(): String = replace("\r", "\\r").replace("\n", "\\n")
fun String.escapeControlChars(): String = replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t").replace("[\\p{Cntrl}]".toRegex(), "\\?")
fun String.escapeBackslash(): String = replace("\\", "\\\\")
fun String.escapeDoubleQuote(): String = replace("\"", "\\\"")
fun String.escapeSingleQuote(): String = replace("'", "\'")
fun String.escapeBacktick(): String = replace("`", "\\`")
fun String.escapeDollar(): String = replace("$", "\\$")
fun String.escapeSingleQuoteForShell(): String = replace("'", "'\"'\"'")
fun String.escapeProcentForPrintf(): String = replace("%", "%%")
fun String.endingWithFileSeparator(): String = if (isNotEmpty() && (last() != fileSeparatorChar())) this + fileSeparator() else this
fun prefixWithSudo(text: String, sudo: Boolean): String = if (sudo) "sudo $text" else text
fun sudoAsText(sudo: Boolean): String = if (sudo) "sudo" else ""

// --------------  Functions for system related properties    -----------------
fun fileSeparator(): String = File.separator
fun fileSeparatorChar(): Char = File.separatorChar
fun newline(): String = System.lineSeparator()
fun hostUserHome(): String = System.getProperty("user.home") + fileSeparator()


/**
 * Put String between double quotes and escapes chars that need to be escaped (by backslash) for use in Unix Shell String
 * I.e. the following chars are escaped: backslash, backtick, double quote, dollar
 */
fun String.escapeAndEncloseByDoubleQuoteForShell(): String {
    return "\"" + this.escapeForShell() + "\""
}

fun String.escapeForShell(): String {
    // see https://www.shellscript.sh/escape.html
    return this.escapeBackslash().escapeBacktick().escapeDoubleQuote().escapeDollar()
}


/**
 * Returns an echo command for the given String, which will be escaped for the bash
 */
internal fun echoCommandForText(text: String): String {
    return "echo -n ${text.escapeAndEncloseByDoubleQuoteForShell()}"
}


/**
 * Returns content of a local file as String (from the filesystem on the machine where provs has been initiated)
 */
internal fun getLocalFileContent(fullyQualifiedLocalFileName: String, sudo: Boolean = false): String {
    val content = local().fileContent(fullyQualifiedLocalFileName, sudo)
    check(content != null, { "Could not retrieve content from file: $fullyQualifiedLocalFileName" })
    return content
}


/**
 * Returns content of a resource file as String
 */
fun getResourceAsText(path: String): String {
    val resource = Thread.currentThread().contextClassLoader.getResource(path)
    requireNotNull(resource) { "Resource $path not found" }
    return resource.readText()
}


/**
 * Returns content of a resource file as String with the variables resolved
 */
fun getResourceResolved(path: String, values: Map<String, String>): String {
    val resource = Thread.currentThread().contextClassLoader.getResource(path)
    requireNotNull(resource) { "Resource $path not found" }
    return resource.readText().resolve(values)
}


/**
 * Returns a String in which placeholders (e.g. $var or ${var}) are replaced by the specified values.
 * This function can be used for resolving templates at RUNTIME (e.g. for templates read from files) as
 * for compile time this functionality is already provided by the compiler out-of-the-box.
 *
 * For a usage example see the corresponding test.
 */
fun String.resolve(values: Map<String, String>): String {

    val result = StringBuilder()

    val matcherSimple = "\\$([a-zA-Z_][a-zA-Z_0-9]*)"           // simple placeholder e.g. $var
    val matcherWithBraces = "\\$\\{([a-zA-Z_][a-zA-Z_0-9]*)}"   // placeholder within braces e.g. ${var}

    // match a placeholder (like $var or ${var}) or ${'$'} (escaped dollar)
    val allMatches = Regex("$matcherSimple|$matcherWithBraces|\\\$\\{'(\\\$)'}").findAll(this)

    var position = 0
    allMatches.forEach {
        val range = it.range
        val placeholder = this.substring(range)
        val variableName = it.groups.filterNotNull()[1].value
        val newText =
            if ("\${'\$'}" == placeholder) "$"
            else values[variableName] ?: throw IllegalArgumentException("Could not resolve placeholder $placeholder")
        result.append(this.substring(position, range.start)).append(newText)
        position = range.last + 1
    }
    result.append(this.substring(position))
    return result.toString()
}

/**
 * Returns default local Prov instance.
 */
@Suppress("unused")  // used by other libraries resp. KotlinScript
fun local(): Prov {
    return Prov.defaultInstance()
}


/**
 * Returns Prov instance for remote host with remote user with provided password.
 * If password is null, connection is done by ssh-key.
 * Platform (Linux, etc) must be provided if different from local platform.
 */
@Api  // used by other libraries resp. KotlinScript
fun remote(host: String, remoteUser: String, password: Secret? = null, platform: OS = OS.LINUX): Prov {
    require(host.isNotEmpty(), { "Host must not be empty." })
    require(remoteUser.isNotEmpty(), { "Remote user must not be empty." })

    return Prov.newInstance(RemoteProcessor(InetAddress.getByName(host), remoteUser, password), platform = platform)
}


/**
 * Returns Prov instance which executes its tasks in a local docker container with name containerName.
 * If a container with the given name is running already, it'll be reused if parameter useExistingContainer is set to true.
 * If a container is reused, it is not checked if it has the correct, specified image.
 * Determines automatically if sudo is required if sudo is null, otherwise the specified sudo is used
 */
@Api  // used by other libraries resp. KotlinScript
fun docker(
    containerName: String = "provs_default",
    useExistingContainer: Boolean = true,
    imageName: String = "ubuntu",
    sudo: Boolean? = null
): Prov {

    val sudoRequired = sudo ?: checkSudoRequiredForDocker()

    local().provideContainer(containerName, imageName)

    return Prov.newInstance(
        ContainerUbuntuHostProcessor(
            containerName,
            imageName,
            if (useExistingContainer)
                ContainerStartMode.USE_RUNNING_ELSE_CREATE
            else
                ContainerStartMode.CREATE_NEW_KILL_EXISTING,
            sudo = sudoRequired
        )
    )
}


/**
 * Returns true if sudo is required to run docker locally, otherwise returns false.
 * Throws an IllegalStateException if docker cannot be run locally at all.
 */
fun checkSudoRequiredForDocker(): Boolean {
    return if (local().chk("docker -v")) {
        false
    } else if (local().chk("sudo docker -v")) {
        true
    } else {
        throw IllegalStateException("Docker could not be run.")
    }
}