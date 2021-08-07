package org.domaindrivenarchitecture.provs.core.processors

import org.domaindrivenarchitecture.provs.core.ProgressType
import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.docker.provideContainer
import org.domaindrivenarchitecture.provs.core.escapeAndEncloseByDoubleQuoteForShell
import org.domaindrivenarchitecture.provs.core.platforms.SHELL
import org.domaindrivenarchitecture.provs.core.tags.Api

enum class ContainerStartMode {
    USE_RUNNING_ELSE_CREATE,
    CREATE_NEW_KILL_EXISTING,
    CREATE_NEW_FAIL_IF_EXISTING
}

enum class ContainerEndMode {
    EXIT_AND_REMOVE,
    KEEP_RUNNING
}

open class ContainerUbuntuHostProcessor(
    private val containerName: String = "default_provs_container",
    @Api // suppress false positive warning
    private val dockerImage: String = "ubuntu",
    @Api // suppress false positive warning
    private val startMode: ContainerStartMode = ContainerStartMode.USE_RUNNING_ELSE_CREATE,
    private val endMode: ContainerEndMode = ContainerEndMode.KEEP_RUNNING,
    @Api // suppress false positive warning
    private val sudo: Boolean = true
) : Processor {
    private val dockerCmd = if (sudo) "sudo docker " else "docker "
    private var localExecution = LocalProcessor()
    private var a = Prov.newInstance(name = "LocalProcessor for Docker operations", progressType = ProgressType.NONE)

    init {
        val r = a.provideContainer(containerName, dockerImage, startMode, sudo)
        if (!r.success)
            throw RuntimeException("Could not start docker image: " + r.toString(), r.exception)
    }

    override fun x(vararg args: String): ProcessResult {
        return localExecution.x("sh", "-c", dockerCmd + "exec $containerName " + buildCommand(*args))
    }

    override fun xNoLog(vararg args: String): ProcessResult {
        return localExecution.xNoLog("sh", "-c", dockerCmd + "exec $containerName " + buildCommand(*args))
    }

    private fun exitAndRm() {
        localExecution.x(SHELL, "-c", dockerCmd + "stop $containerName")
        localExecution.x(SHELL, "-c", dockerCmd + "rm $containerName")
    }

    private fun quoteString(s: String): String {
        return s.escapeAndEncloseByDoubleQuoteForShell()
    }

    private fun buildCommand(vararg args: String) : String {
        return if (args.size == 1) quoteString(args[0]) else
            if (args.size == 3 && SHELL == args[0] && "-c" == args[1]) SHELL + " -c " + quoteString(args[2])
            else args.joinToString(separator = " ")
    }

    protected fun finalize() {
        if (endMode == ContainerEndMode.EXIT_AND_REMOVE) {
            exitAndRm()
        }
    }
}
