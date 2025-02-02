package org.domaindrivenarchitecture.provs.framework.core

import org.domaindrivenarchitecture.provs.framework.core.platforms.SHELL
import org.domaindrivenarchitecture.provs.framework.core.platforms.UbuntuProv
import org.domaindrivenarchitecture.provs.framework.core.processors.LocalProcessor
import org.domaindrivenarchitecture.provs.framework.core.processors.Processor
import org.slf4j.LoggerFactory


enum class ProgressType { NONE, DOTS, BASIC, FULL_LOG }
enum class ResultMode { OPTIONAL, LAST, ALL, FAILEXIT }
enum class OS { LINUX }


private const val RESULT_PREFIX = ">  "
private const val NOT_IMPLEMENTED = "Not implemented"


/**
 * This main class offers methods to execute shell commands.
 * The commands are executed by the provided processor,
 * e.g. a LocalProcessor will execute them locally, a RemoteUbuntuProcessor remotely (via ssh), etc.
 */
open class Prov protected constructor(
    private val processor: Processor,
    private val instanceName: String? = null,
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

    private var level = 0
    private var exit = false
    private var runInContainerWithName: String? = null

    private val internalResults = arrayListOf<ResultLine>()
    private val infoTexts = arrayListOf<String>()

    /**
     * A session is the top-level execution unit in provs. A session can contain tasks.
     * Returns success if no sub-tasks are called or if all subtasks finish with success.
     */
    fun session(taskLambda: Prov.() -> ProvResult): ProvResult {
        if (level > 0) {
            throw IllegalStateException("A session can only be created on the top-level and may not be included in another session or task.")
        }
        return evaluate(ResultMode.ALL, "session") { taskLambda() }
    }

    /**
     * A task is the fundamental execution unit. In the results overview it is represented by one line with a success or failure result.
     * Returns success if all sub-tasks finished with success or if no sub-tasks are called at all.
     */
    fun task(name: String? = null, taskLambda: Prov.() -> Unit): ProvResult {
        printDeprecationWarningIfLevel0("task")
        return evaluate(ResultMode.ALL, name) { taskLambda(); ProvResult(true) }
    }

    /**
     * Same as task above but the lambda parameter must have a ProvResult as return type.
     * The returned ProvResult is included in the success resp. failure evaluation,
     * i.e. if the returned ProvResult from the lambda fails, the returned ProvResult from
     * taskWithResult also fails, else success depends on potentially called sub-tasks.
     */
    fun taskWithResult(name: String? = null, taskLambda: Prov.() -> ProvResult): ProvResult {
        printDeprecationWarningIfLevel0("taskWithResult")
        return evaluate(ResultMode.ALL, name) { taskLambda() }
    }

    /**
     * defines a task, which returns the returned result from the lambda, the results of sub-tasks are not considered
     */
    fun requireLast(name: String? = null, taskLambda: Prov.() -> ProvResult): ProvResult {
        printDeprecationWarningIfLevel0("requireLast")
        return evaluate(ResultMode.LAST, name) { taskLambda() }
    }

    /**
     * Defines a task, which always returns success.
     */
    fun optional(name: String? = null, taskLambda: Prov.() -> ProvResult): ProvResult {
        printDeprecationWarningIfLevel0("optional")
        return evaluate(ResultMode.OPTIONAL, name) { taskLambda() }
    }

    /**
     * Defines a task, which exits the overall execution on failure result of the taskLambda.
     */
    fun exitOnFailure(taskLambda: Prov.() -> ProvResult): ProvResult {
        printDeprecationWarningIfLevel0("exitOnFailure")
        return evaluate(ResultMode.FAILEXIT) { taskLambda() }
    }

    /**
     * Runs the provided task in the specified (running) container
     */
    fun taskInContainer(containerName: String, taskLambda: Prov.() -> ProvResult): ProvResult {
        printDeprecationWarningIfLevel0("taskInContainer")
        runInContainerWithName = containerName
        val res = evaluate(ResultMode.ALL) { taskLambda() }
        runInContainerWithName = null
        return res
    }

    /**
     * Executes a program with (optional) parameters.
     * args[0] contains the program name, the other args (if provided) specify the parameters.
     */
    fun exec(vararg args: String): ProvResult {
        val cmd = runInContainerWithName?.let { execInContainer(it, *args) } ?: args
        val result = processor.exec(*cmd)
        return ProvResult(
            success = (result.exitCode == 0),
            cmd = result.argsToString(),
            out = result.out,
            err = result.err
        )
    }

    /**
     * Executes a program with (optional) parameters without logging (e.g. to be used if secrets are involved)
     * args[0] contains the program name, the other args (if provided) specify the parameters.
     */
    fun execNoLog(vararg s: String): ProvResult {
        val cmd = runInContainerWithName?.let { execInContainer(it, *s) } ?: s
        val result = processor.execNoLog(*cmd)
        return ProvResult(
            success = (result.exitCode == 0),
            cmd = "***",
            out = result.out,
            err = "***"
        )
    }

    /**
     * Executes a program with (optional) parameters in the specified  container.
     * args[0] contains the program name, the other args (if provided) specify the parameters.
     */
    protected open fun execInContainer(containerName: String, vararg args: String): Array<String> {
        throw Exception(NOT_IMPLEMENTED)
    }

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
    fun getSecret(command: String, removeNewlineSuffix: Boolean = false): Secret? {
        val result = cmdNoLog(command)
        return if (result.success && result.out != null) {
            addProvResult(true, getCallingMethodName())
            val plainSecret =
                if (removeNewlineSuffix && result.out.takeLast(1) == "\n") result.out.dropLast(1) else result.out
            Secret(plainSecret)
        } else {
            addProvResult(false, getCallingMethodName(), err = result.err, exception = result.exception)
            null
        }
    }


    /**
     * Adds a ProvResult to the overall success evaluation.
     * Intended for use in methods which do not automatically add results.
     */
    @Deprecated("since 0.39.7", replaceWith = ReplaceWith("addProvResult", ))
    fun addResultToEval(result: ProvResult) = taskWithResult {
        result
    }

    /**
     * Adds a ProvResult to the overall success evaluation.
     * Intended for use in methods which do not automatically add results.
     */
    fun addProvResult(
        success: Boolean,
        cmd: String? = null,
        out: String? = null,
        err: String? = null,
        exception: Exception? = null,
        exit: String? = null
    ) = taskWithResult {
        ProvResult(success, cmd, out, err, exception, exit)
    }

    /**
     * Executes multiple shell commands. Each command must be in its own line.
     * Multi-line commands within the script are not supported.
     * Empty lines and comments (all text behind # in a line) are supported, i.e. they are ignored.
     */
    fun sh(script: String, dir: String? = null, sudo: Boolean = false) = taskWithResult {
        val lines = script.trimIndent().replace("\\\n", "").replace("\r\n", "\n").split("\n")
        val linesWithoutComments = lines.stream().map { it.split("#")[0] }
        val linesNonEmpty = linesWithoutComments.filter { it.trim().isNotEmpty() }

        var success = true

        for (cmd in linesNonEmpty) {
            if (success) {
                success = cmd(cmd, dir, sudo).success
            }
        }
        ProvResult(success)
    }

    fun addInfoText(text: String) {
        infoTexts.add(text)
    }

    // =====================================  private functions  ==================================

    /**
     * Provides task evaluation, i.e. computes a ProvResult based on the provided resultMode,
     * on the returned ProvResult from the task as well as on the results from executed subtasks (if there are).
     */
    private fun evaluate(resultMode: ResultMode, name: String? = null, taskLambda: Prov.() -> ProvResult): ProvResult {

        // init
        if (level == 0) {
            internalResults.clear()
            exit = false
            initProgress()

            processor.open()
        }

        // pre-handling
        val resultIndex = internalResults.size
        val taskName = name ?: getCallingMethodName()
        val internalResult = ResultLine(level, taskName, null)
        internalResults.add(internalResult)

        level++

        // call the actual task lambda
        val resultOfTaskLambda = if (!exit) {
            progress(internalResult)
            @Suppress("UNUSED_EXPRESSION") // false positive
            taskLambda()
        } else {
            ProvResult(false, out = "Exiting due to failure and mode FAILEXIT")
        }

        level--

        // post-handling
        // determine result
        val returnValue =
            if (resultMode == ResultMode.LAST) {
                if (internalResultIsLeaf(resultIndex) || taskName == "cmd" || taskName?.replace(" (requireLast)", "") == "repeatTaskUntilSuccess") {
                    // for a leaf (task with mo subtask) or tasks "cmd" resp. "repeatUntilTrue" provide also out and err of original results
                    // because results of cmd and leafs are not included in the reporting
                    // and the caller of repeatUntilTrue might need to see the complete result (incl. out and err) and not only success value
                    resultOfTaskLambda.copy()
                } else {
                    // just pass success value, no other data of the original result
                    ProvResult(resultOfTaskLambda.success)
                }
            } else if (resultMode == ResultMode.ALL) {
                // leaf
                if (internalResultIsLeaf(resultIndex)) resultOfTaskLambda.copy()
                // evaluate subcalls' results
                else ProvResult((cumulativeSuccessSublevel(resultIndex) ?: false) && resultOfTaskLambda.success)
            } else if (resultMode == ResultMode.OPTIONAL) {
                ProvResult(true)
            } else if (resultMode == ResultMode.FAILEXIT) {
                return if (resultOfTaskLambda.success) {
                    ProvResult(true)
                } else {
                    exit = true
                    ProvResult(false)
                }
            } else {
                ProvResult(false, err = "mode unknown")
            }

        // removes potential prefix from cmd in ProvResult, e.g. removes "/bin/bash -c "
        fun cleanedResult(result: ProvResult): ProvResult { return result.copy(cmd = returnValue.cmd?.replace("[" + SHELL + ", -c, ", "[")) }

        val resultValueWithCmdCleanedUp = cleanedResult(returnValue)
        internalResults[resultIndex].provResult = resultValueWithCmdCleanedUp

        // Add failure result to output if not yet included,
        // which is the case if the result was not part of another subtask but created and returned by the lambda itself.
        // Success results do not need to be added here as they don't change the overall success evaluation,
        // whereas the failure results may have a useful error message, which should be in the output.
        // Only direct result objects are added, but not result objects that were passed from a subtask as they are already handled in the subtask.
        if (!resultOfTaskLambda.success && (resultIndex < internalResults.size - 1) && (resultOfTaskLambda != internalResults[resultIndex + 1].provResult)) {
            internalResults.add(ResultLine(level + 1, name + " (returned result)", cleanedResult(resultOfTaskLambda)))
        }

        if (level == 0) {
            endProgress()
            processor.close()
            printResults()
        }

        return returnValue
    }


    /**
     * Returns true if the task at the specified index has no subtasks.
     * I.e. if the task is the last one or if level of the next task is the same or less (which means same level or "higher" in the tree)
     */
    private fun internalResultIsLeaf(resultIndex: Int): Boolean {
        return (resultIndex >= internalResults.size - 1 || internalResults[resultIndex].level >= internalResults[resultIndex + 1].level)
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
    private val ANSI_BRIGHT_YELLOW = "\u001B[93m"
    private val ANSI_BRIGHT_GREEN = "\u001B[92m"
    private val ANSI_BRIGHT_BLUE = "\u001B[94m"
    private val ANSI_GRAY = "\u001B[90m"

    private fun printResults() {
        println(
            "============================================== SUMMARY " +
                    (if (instanceName != null) "($instanceName) " else "") +
                    "============================================="
        )
        val successPerLevel = arrayListOf<Boolean>()
        for (result in internalResults) {
            val currentLevel = result.level

            // store level success
            val successOfCurrentLevel = result.provResult?.success ?: false
            if (currentLevel < successPerLevel.size) {
                successPerLevel[currentLevel] = successOfCurrentLevel
            } else {
                successPerLevel.add(successOfCurrentLevel)
            }

            // check success levels above; if a level above succeeded then a failure in this level will be considered optional (i.e. marked yellow instead of red)
            val successOfLevelsAbove = levelsAboveContainsSuccess(successPerLevel, currentLevel)
            println(result.toString().escapeControlChars().formattedAsResultLine(successOfLevelsAbove))
        }
        if (internalResults.size > 1) {
            println("----------------------------------------------------------------------------------------------------")
            println("Overall " + internalResults[0].toString().take(10).formattedAsResultLine())
        }
        printInfoTexts()
        println("============================================ SUMMARY END ===========================================" + newline())
    }

    private fun levelsAboveContainsSuccess(successPerLevel: ArrayList<Boolean>, currentLevel: Int): Boolean {
        var success = false
        for (i in 0..currentLevel - 1) {
            success = success || successPerLevel[i]
        }
        return success
    }

    private fun String.formattedAsResultLine(showFailedInYellow: Boolean = false): String {
        val failedColor = if (showFailedInYellow) ANSI_BRIGHT_YELLOW else ANSI_BRIGHT_RED
        return this
            .replaceFirst("${RESULT_PREFIX}Success", RESULT_PREFIX + ANSI_BRIGHT_GREEN + "Success" + ANSI_RESET)
            .replaceFirst("${RESULT_PREFIX}FAILED", RESULT_PREFIX + failedColor + "FAILED" + ANSI_RESET)
            .replace("${RESULT_PREFIX}executing...", RESULT_PREFIX + ANSI_GRAY + "executing..." + ANSI_RESET)
            .take(400)
    }


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

    private fun printInfoTexts() {
        if (infoTexts.isNotEmpty()) {
            println("+++++++++++++++++++++++++++++++++++  ${ANSI_BRIGHT_BLUE}Additional information$ANSI_RESET  +++++++++++++++++++++++++++++++++++++++")
            for (text in infoTexts) {
                println(text)
            }
        }
    }

    fun printDeprecationWarningIfLevel0(methodName: String) {
        if (level == 0 && progressType != ProgressType.NONE) {
            println("WARNING: method $methodName should not be used at top-level, use method <session> instead.")
        }
    }
}


internal data class ResultLine(val level: Int, val method: String?, var provResult: ProvResult?) {
    override fun toString(): String {
        val provResult = provResult
        return if (provResult != null) {
            prefix(level) + (if (provResult.success) "Success -- " else "FAILED  -- ") +
                    method + " " + (provResult.cmd ?: "") +
                    (if (!provResult.success && provResult.err != null) " -- Error: " + provResult.err.escapeControlChars() else "")
        } else
            prefix(level) + method + " " + "... in progress ... "

    }

    fun inProgress(): String {
        return prefix(level) + "executing... -- " + method
    }

    private fun prefix(level: Int): String {
        return "---".repeat(level) + RESULT_PREFIX
    }
}
