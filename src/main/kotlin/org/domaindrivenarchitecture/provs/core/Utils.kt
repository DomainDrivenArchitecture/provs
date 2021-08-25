package org.domaindrivenarchitecture.provs.core

import org.domaindrivenarchitecture.provs.core.docker.provideContainer
import org.domaindrivenarchitecture.provs.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.core.processors.ContainerUbuntuHostProcessor
import org.domaindrivenarchitecture.provs.core.processors.RemoteProcessor
import org.domaindrivenarchitecture.provs.core.tags.Api
import java.io.File
import java.net.InetAddress

/**
 * Returns the name of the calling function but excluding some functions of the prov framework
 * in order to return the "real" calling function.
 * Note: names of inner functions (i.e. which are defined inside other functions) are not
 * supported in the sense that always the name of the outer function is returned instead.
 */
fun getCallingMethodName(): String? {
    val offsetVal = 1
    val exclude = arrayOf("def", "record", "invoke", "invoke0", "handle", "def\$default", "addResultToEval")
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


fun String.escapeNewline(): String = this.replace("\r", "\\r").replace("\n", "\\n")
fun String.escapeControlChars(): String = this.replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t").replace("[\\p{Cntrl}]".toRegex(), "\\?")
fun String.escapeBackslash(): String = this.replace("\\", "\\\\")
fun String.escapeDoubleQuote(): String = this.replace("\"", "\\\"")
fun String.escapeSingleQuote(): String = this.replace("'", "\'")
fun String.escapeSingleQuoteForShell(): String = this.replace("'", "'\"'\"'")
fun String.escapeProcentForPrintf(): String = this.replace("%", "%%")

// see https://www.shellscript.sh/escape.html
fun String.escapeAndEncloseByDoubleQuoteForShell(): String {
    return "\"" + this.escapeBackslash().replace("`", "\\`").escapeDoubleQuote().replace("$", "\\$") + "\""
}

fun hostUserHome(): String = System.getProperty("user.home") + fileSeparator()
fun newline(): String = System.getProperty("line.separator")
fun fileSeparator(): String = File.separator


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
 * Returns Prov instance running in a local docker container with name containerName.
 * A potentially existing container with the same name is reused by default resp. if
 * parameter useExistingContainer is set to true.
 * If a new container needs to be created, on Linux systems the image _ubuntu_ is used.
 */
@Api  // used by other libraries resp. KotlinScript
fun docker(containerName: String = "provs_default", useExistingContainer: Boolean = true): Prov {

    val os = System.getProperty("os.name")

    if ("Linux".equals(os)) {
        val defaultDockerImage = "ubuntu"

        local().provideContainer(containerName, defaultDockerImage)

        return Prov.newInstance(
            ContainerUbuntuHostProcessor(
                containerName,
                defaultDockerImage,
                if (useExistingContainer)
                    ContainerStartMode.USE_RUNNING_ELSE_CREATE
                else
                    ContainerStartMode.CREATE_NEW_KILL_EXISTING
            )
        )
    } else {
        throw RuntimeException("ERROR: method docker() is currently not supported for " + os)
    }
}
