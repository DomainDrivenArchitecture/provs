package org.domaindrivenarchitecture.provs.core

import org.domaindrivenarchitecture.provs.core.platforms.SHELL
import org.domaindrivenarchitecture.provs.core.platforms.UbuntuProv
import org.domaindrivenarchitecture.provs.core.processors.LocalProcessor
import org.domaindrivenarchitecture.provs.core.processors.Processor
import org.slf4j.LoggerFactory


enum class ProgressType { NONE, DOTS, BASIC, FULL_LOG }
enum class ResultMode { NONE, LAST, ALL, FAILEXIT }
enum class OS { LINUX }


/**
 * This main class offers methods to execute shell commands.
 * The commands are executed locally, remotely (via ssh) or in a docker container
 * depending on the processor which is passed to the constructor.
 */
open class Prov protected constructor(
    private val processor: Processor,
    val name: String? = null,
    private val progressType: ProgressType = ProgressType.BASIC
) {
    init {
        if (progressType == ProgressType.FULL_LOG) {
            val log = LoggerFactory.getILoggerFactory()
                .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as (ch.qos.logback.classic.Logger)
            log.level = ch.qos.logback.classic.Level.INFO
        }
    }

    companion object Factory {

        private lateinit var defaultProvInstance: Prov

        fun defaultInstance(): Prov {
            return if (Factory::defaultProvInstance.isInitialized) {
                defaultProvInstance
            } else {
                defaultProvInstance = newInstance(name = "default instance", platform = OS.LINUX)
                defaultProvInstance
            }
        }

        fun newInstance(
            processor: Processor = LocalProcessor(),
            name: String? = null,
            progressType: ProgressType = ProgressType.BASIC,
            platform: OS = OS.LINUX
        ): Prov {

            return when {
                (platform == OS.LINUX) -> UbuntuProv(processor, name, progressType)
                else -> throw Exception("OS not supported")
            }
        }
    }


    private val internalResults = arrayListOf<ResultLine>()
    private var level = 0
    private var previousLevel = 0
    private var exit = false
    private var runInContainerWithName: String? = null


    /**
     * defines a task with default success behavior, i.e. returns success if all subtasks finished with success.
     * Same as requireAll.
     */
    fun def(a: Prov.() -> ProvResult): ProvResult {
        return handle(ResultMode.ALL) { a() }
    }

    /**
     * defines a task, which returns success if the the last subtasks or last value returns success
     */
    fun requireLast(a: Prov.() -> ProvResult): ProvResult {
        return handle(ResultMode.LAST) { a() }
    }

    /**
     * defines a task, which always returns success
     */
    fun optional(a: Prov.() -> ProvResult): ProvResult {
        return handle(ResultMode.NONE) { a() }
    }

    /**
     * defines a task, which returns success if all subtasks finished with success
     */
    fun requireAll(a: Prov.() -> ProvResult): ProvResult {
        return handle(ResultMode.ALL) { a() }
    }

    /**
     * defines a task, which exits the overall execution on failure
     */
    fun exitOnFailure(a: Prov.() -> ProvResult): ProvResult {
        return handle(ResultMode.FAILEXIT) { a() }
    }

    // todo: add sudo and update test
    fun inContainer(containerName: String, a: Prov.() -> ProvResult): ProvResult {
        runInContainerWithName = containerName
        val res = handle(ResultMode.ALL) { a() }
        runInContainerWithName = null
        return res
    }


    /**
     *  execute program with parameters
     */
    fun xec(vararg s: String): ProvResult {
        val cmd = runInContainerWithName?.let { cmdInContainer(it, *s) } ?: s
        val result = processor.x(*cmd)
        return ProvResult(
            success = (result.exitCode == 0),
            cmd = result.argsToString(),
            out = result.out,
            err = result.err
        )
    }

    /**
     *  execute program with parameters without logging (to be used if secrets are involved)
     */
    fun xecNoLog(vararg s: String): ProvResult {
        val cmd = runInContainerWithName?.let { cmdInContainer(it, *s) } ?: s
        val result = processor.xNoLog(*cmd)
        return ProvResult(
            success = (result.exitCode == 0),
            cmd = "***",
            out = result.out,
            err = "***"
        )
    }


    private val NOT_IMPLEMENTED = "Not implemented"

    /**
     * Executes a command by using the shell.
     * Be aware: Executing shell commands that incorporate unsanitized input from an untrusted source
     * makes a program vulnerable to shell injection, a serious security flaw which can result in arbitrary command execution.
     * Thus, the use of this method is strongly discouraged in cases where the command string is constructed from external input.
     */
    open fun cmd(cmd: String, dir: String? = null, sudo: Boolean = false): ProvResult {
        throw Exception(NOT_IMPLEMENTED)
    }


    /**
     * Same as method cmd but without logging of the result/output, should be used e.g. if secrets are involved.
     * Attention: only result is NOT logged the executed command still is.
     */
    open fun cmdNoLog(cmd: String, dir: String? = null, sudo: Boolean = false): ProvResult {
        throw Exception(NOT_IMPLEMENTED)
    }


    /**
     * Same as method cmd but without evaluating the result for the overall success.
     * Can be used e.g. for checks which might succeed or fail but where failure should not influence overall success
     */
    open fun cmdNoEval(cmd: String, dir: String? = null, sudo: Boolean = false): ProvResult {
        throw Exception(NOT_IMPLEMENTED)
    }


    /**
     * Executes command cmd and returns true in case of success else false.
     * The success resp. failure is not evaluated, i.e. it is not taken into account for the overall success.
     */
    fun chk(cmd: String, dir: String? = null): Boolean {
        return cmdNoEval(cmd, dir).success
    }


    /**
     * Retrieve a secret by executing the given command.
     * Returns the result of the command as secret.
     */
    fun getSecret(command: String): Secret? {
        val result = cmdNoLog(command)
        return if (result.success && result.out != null) {
            addResultToEval(ProvResult(true, getCallingMethodName()))
            Secret(result.out)
        } else {
            addResultToEval(ProvResult(false, getCallingMethodName(), err = result.err, exception = result.exception))
            null
        }
    }


    /**
     * Adds a ProvResult to the overall success evaluation.
     * Intended for use in methods which do not automatically add results.
     */
    fun addResultToEval(result: ProvResult) = requireAll {
        result
    }

    /**
     * Executes multiple shell commands. Each command must be in its own line.
     * Multi-line commands within the script are not supported.
     * Empty lines and comments (all text behind # in a line) are supported, i.e. they are ignored.
     */
    fun sh(script: String, dir: String? = null, sudo: Boolean = false) = def {
        val lines = script.trimIndent().replace("\r\n", "\n").split("\n")
        val linesWithoutComments = lines.stream().map { it.split("#")[0] }
        val linesNonEmpty = linesWithoutComments.filter { it.trim().isNotEmpty() }

        var success = true

        for (cmd in linesNonEmpty) {
            if (success) {
                success = success && cmd(cmd, dir, sudo).success
            }
        }
        ProvResult(success)
    }


    // todo: put logic in subclasses, such as UbuntuProv
    private fun cmdInContainer(containerName: String, vararg args: String): Array<String> {
        return arrayOf(SHELL, "-c", "sudo docker exec $containerName " + buildCommand(*args))
    }

    private fun buildCommand(vararg args: String): String {
        return if (args.size == 1)
            args[0].escapeAndEncloseByDoubleQuoteForShell()
        else
            if (args.size == 3 && SHELL.equals(args[0]) && "-c".equals(args[1]))
                SHELL + " -c " + args[2].escapeAndEncloseByDoubleQuoteForShell()
            else
                args.joinToString(separator = " ")
    }


    /**
     * Provides result handling, e.g. gather results for result summary
     */
    private fun handle(mode: ResultMode, a: Prov.() -> ProvResult): ProvResult {

        // init
        if (level == 0) {
            internalResults.clear()
            previousLevel = -1
            exit = false
            initProgress()
        }

        // pre-handling
        val resultIndex = internalResults.size
        val method = getCallingMethodName()
        val internalResult = ResultLine(level, method, null)
        internalResults.add(internalResult)

        previousLevel = level

        level++

        // call the actual function
        val res = if (!exit) {
            progress(internalResult)
            @Suppress("UNUSED_EXPRESSION") // false positive
            a()
        } else {
            ProvResult(false, out = "Exiting due to failure and mode FAILEXIT")
        }

        level--

        // post-handling
        val returnValue =
            if (mode == ResultMode.LAST) {
                if (internalResultIsLeaf(resultIndex) || method == "cmd")
                    res.copy() else ProvResult(res.success)
            } else if (mode == ResultMode.ALL) {
                // leaf
                if (internalResultIsLeaf(resultIndex)) res.copy()
                // evaluate subcalls' results
                else ProvResult(cumulativeSuccessSublevel(resultIndex) ?: false)
            } else if (mode == ResultMode.NONE) {
                ProvResult(true)
            } else if (mode == ResultMode.FAILEXIT) {
                return if (res.success) {
                    ProvResult(true)
                } else {
                    exit = true
                    ProvResult(false)
                }
            } else {
                ProvResult(false, err = "mode unknown")
            }

        previousLevel = level

        internalResults[resultIndex].provResult = returnValue

        if (level == 0) {
            endProgress()
            processor.close()
            printResults()
        }

        return returnValue
    }


    private fun internalResultIsLeaf(resultIndex: Int): Boolean {
        return !(resultIndex < internalResults.size - 1 && internalResults[resultIndex + 1].level > internalResults[resultIndex].level)
    }


    private fun cumulativeSuccessSublevel(resultIndex: Int): Boolean? {
        val currentLevel = internalResults[resultIndex].level
        var res: Boolean? = null
        var i = resultIndex + 1
        while (i < internalResults.size && internalResults[i].level > currentLevel) {
            if (internalResults[i].level == currentLevel + 1) {
                res =
                    if (res == null) internalResults[i].provResult?.success else res && (internalResults[i].provResult?.success
                        ?: false)
            }
            i++
        }
        return res
    }


    private val ANSI_RESET = "\u001B[0m"
    private val ANSI_BRIGHT_RED = "\u001B[91m"
    private val ANSI_BRIGHT_GREEN = "\u001B[92m"
    // uncomment if needed
    //    val ANSI_BLACK = "\u001B[30m"
    //    val ANSI_RED = "\u001B[31m"
    //    val ANSI_GREEN = "\u001B[32m"
    //    val ANSI_YELLOW = "\u001B[33m"
    //    val ANSI_BLUE = "\u001B[34m"
    //    val ANSI_PURPLE = "\u001B[35m"
    //    val ANSI_CYAN = "\u001B[36m"
    //    val ANSI_WHITE = "\u001B[37m"
    val ANSI_GRAY = "\u001B[90m"

    private fun printResults() {
        println(
            "============================================== SUMMARY " + (if (name != null) "(" + name + ") " else "") +
                    "============================================== "
        )
        for (result in internalResults) {
            println(
                result.toString().escapeNewline()
                    .replace("Success --", ANSI_BRIGHT_GREEN + "Success" + ANSI_RESET + " --")
                    .replace("FAILED --", ANSI_BRIGHT_RED + "FAILED" + ANSI_RESET + " --")
            )
        }
        if (internalResults.size > 1) {
            println("----------------------------------------------------------------------------------------------------- ")
            println(
                "Overall " + internalResults[0].toString().take(10)
                    .replace("Success", ANSI_BRIGHT_GREEN + "Success" + ANSI_RESET)
                    .replace("FAILED", ANSI_BRIGHT_RED + "FAILED" + ANSI_RESET)
            )
        }
        println("============================================ SUMMARY END ============================================ " + newline())
    }

    private fun String.formattedAsResultLine(): String = this
        .replace("Success", ANSI_BRIGHT_GREEN + "Success" + ANSI_RESET)
        .replace("FAILED", ANSI_BRIGHT_RED + "FAILED" + ANSI_RESET)
        .replace("executing...", ANSI_GRAY + "executing..." + ANSI_RESET)


    private fun initProgress() {
        if ((progressType == ProgressType.DOTS) || (progressType == ProgressType.BASIC)) {
            println("---------- Processing started ----------")
            System.out.flush()
        }
    }

    private fun progress(line: ResultLine) {
        if (progressType == ProgressType.DOTS) {
            print(".")
            System.out.flush()
        } else if (progressType == ProgressType.BASIC) {
            val shortLine = line.inProgress()
            if (!shortLine.endsWith("cmd") && !shortLine.endsWith("sh")) {
                println(shortLine.formattedAsResultLine())
                System.out.flush()
            }
        }
    }

    private fun endProgress() {
        if ((progressType == ProgressType.DOTS) || (progressType == ProgressType.BASIC)) {
            println("---------- Processing completed ----------")
        }
    }

}


internal data class ResultLine(val level: Int, val method: String?, var provResult: ProvResult?) {
    override fun toString(): String {
        val provResult = provResult
        return if (provResult != null) {
            prefix(level) + (if (provResult.success) "Success -- " else "FAILED -- ") +
                    method + " " + (provResult.cmd ?: "") +
                    (if (!provResult.success && provResult.err != null) " -- Error: " + provResult.err.escapeNewline() else "")
        } else
            prefix(level) + method + " " + "... in progress ... "

    }

    fun inProgress(): String {
        return prefix(level) + "executing... -- " + method
    }

    private fun prefix(level: Int): String {
        return "---".repeat(level) + ">  "
    }
}

fun Prov.myfu() = def {
    cmd("echo asdf222")
}
fun main() {

    local().def {
        cmd("echo asdfasdf")
        myfu()
    }
}