package org.domaindrivenarchitecture.provs.core

import org.domaindrivenarchitecture.provs.core.docker.provideContainer
import org.domaindrivenarchitecture.provs.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.core.processors.ContainerUbuntuHostProcessor
import org.domaindrivenarchitecture.provs.core.processors.RemoteProcessor
import org.domaindrivenarchitecture.provs.core.tags.Api
import java.io.File
import java.net.InetAddress

/**
 * Repeats task until it returns success
 */
fun Prov.repeatTask(times: Int, sleepInSec: Int, func: Prov.() -> ProvResult) = requireLast {
    require(times > 0)
    var result = ProvResult(false, err = "Internal error")  // Will only be returned if function is not executed at all, otherwise func's result is returned
    for (i in 1..times) {
        result = func()
        if (result.success)
            return@requireLast result
        Thread.sleep(sleepInSec * 1000L)
    }
    return@requireLast result
}

/**
 * Returns the name of the calling function but excluding some functions of the prov framework
 * in order to return the "real" calling function.
 * Note: names of inner functions (i.e. which are defined inside other functions) are not
 * supported in the sense that always the name of the outer function is returned instead.
 */
fun getCallingMethodName(): String? {
    val offsetVal = 1
    val exclude = arrayOf("task", "def", "record", "invoke", "invoke0", "handle", "task\$default", "def\$default", "addResultToEval", "handle\$default")
    // suffixes are also ignored as method names but will be added as suffix in the evaluation results
    val suffixes = arrayOf("optional", "requireAll", "requireLast", "inContainer")

    var suffix = ""
    val callingFrame = Thread.currentThread().stackTrace
    for (i in 0 until (callingFrame.size - 1)) {
        if (callingFrame[i].methodName == "getCallingMethodName") {
            var method = callingFrame[i + offsetVal].methodName
            var inc = 0
            while ((method in exclude) or (method in suffixes)) {
                if (method in suffixes && suffix == "") {
                    suffix = method
                }
                inc++
                method = callingFrame[i + offsetVal + inc].methodName
            }
            return method + if (suffix.isBlank()) "" else " ($suffix)"
        }
    }
    return null
}


fun String.escapeNewline(): String = replace("\r", "\\r").replace("\n", "\\n")
fun String.escapeControlChars(): String = replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t").replace("[\\p{Cntrl}]".toRegex(), "\\?")
fun String.escapeBackslash(): String = replace("\\", "\\\\")
fun String.escapeDoubleQuote(): String = replace("\"", "\\\"")
fun String.escapeSingleQuote(): String = replace("'", "\'")
fun String.escapeBacktick(): String = replace("`", "\\`")
fun String.escapeDollar(): String = replace("$", "\\$")
fun String.escapeSingleQuoteForShell(): String = replace("'", "'\"'\"'")
fun String.escapeProcentForPrintf(): String = replace("%", "%%")
fun String.endingWithFileSeparator(): String = if (length > 0 && (last() != fileSeparatorChar())) this + fileSeparator() else this


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
 * Returns an echo command for the given String, which will be escaped for the shell and ADDITIONALLY with newline, tabs, etc replaced by \n, \t, etc
 */
internal fun echoCommandForTextWithNewlinesReplaced(text: String): String {
    return "echo -en ${text.escapeAndEncloseByDoubleQuoteForShell()}"
}


fun fileSeparator(): String = File.separator
fun fileSeparatorChar(): Char = File.separatorChar
fun newline(): String = System.getProperty("line.separator")
fun hostUserHome(): String = System.getProperty("user.home") + fileSeparator()


fun getResourceAsText(path: String): String {
    val resource = Thread.currentThread().contextClassLoader.getResource(path)
    requireNotNull(resource) { "Resource $path not found" }
    return resource.readText()
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
 * Returns Prov instance which eexcutes its tasks in a local docker container with name containerName.
 * If a container with the given name is running already, it'll be reused if parameter useExistingContainer is set to true.
 * If a container is reused, it is not checked if it has the correct, specified image.
 */
@Api  // used by other libraries resp. KotlinScript
fun docker(
    containerName: String = "provs_default",
    useExistingContainer: Boolean = true,
    imageName: String = "ubuntu",
    sudo: Boolean = true
): Prov {

    local().provideContainer(containerName, imageName)

    return Prov.newInstance(
        ContainerUbuntuHostProcessor(
            containerName,
            imageName,
            if (useExistingContainer)
                ContainerStartMode.USE_RUNNING_ELSE_CREATE
            else
                ContainerStartMode.CREATE_NEW_KILL_EXISTING,
            sudo = sudo
        )
    )
}
