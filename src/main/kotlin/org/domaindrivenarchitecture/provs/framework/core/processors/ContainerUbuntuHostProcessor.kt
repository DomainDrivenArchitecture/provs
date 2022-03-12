package org.domaindrivenarchitecture.provs.framework.core.processors

import org.domaindrivenarchitecture.provs.framework.core.ProgressType
import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.docker.provideContainer
import org.domaindrivenarchitecture.provs.framework.core.escapeAndEncloseByDoubleQuoteForShell
import org.domaindrivenarchitecture.provs.framework.core.platforms.SHELL
import org.domaindrivenarchitecture.provs.framework.core.tags.Api

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

    private val hostShell = "/bin/bash"

    override fun exec(vararg args: String): ProcessResult {
        return localExecution.exec(hostShell, "-c", dockerCmd + "exec $containerName " + buildCommand(*args))
    }

    override fun execNoLog(vararg args: String): ProcessResult {
        return localExecution.execNoLog(hostShell, "-c", dockerCmd + "exec $containerName " + buildCommand(*args))
    }

    private fun exitAndRm() {
        localExecution.exec(hostShell, "-c", dockerCmd + "stop $containerName")
        localExecution.exec(hostShell, "-c", dockerCmd + "rm $containerName")
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
