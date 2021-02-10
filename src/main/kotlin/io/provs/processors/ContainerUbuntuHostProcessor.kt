package io.provs.processors

import io.provs.Prov
import io.provs.docker.containerSh
import io.provs.docker.provideContainer
import io.provs.escapeAndEncloseByDoubleQuoteForShell
import io.provs.platforms.SHELL

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
    @Suppress("unused") // suppress false positive warning
    private val dockerImage: String = "ubuntu",
    @Suppress("unused") // suppress false positive warning
    private val startMode: ContainerStartMode = ContainerStartMode.USE_RUNNING_ELSE_CREATE,
    private val endMode: ContainerEndMode = ContainerEndMode.KEEP_RUNNING
) : Processor {
    private var localExecution = LocalProcessor()
    private var a = Prov.newInstance(name = "ContainerUbuntuHostProcessor")

    init {
        val r = a.provideContainer(containerName, dockerImage, startMode)
        if (!r.success)
            throw RuntimeException("Could not start docker image: " + r.toShortString(), r.exception)
    }

    override fun x(vararg args: String): ProcessResult {
        return localExecution.x("sh", "-c", "sudo docker exec $containerName " + buildCommand(*args))
    }

    override fun xNoLog(vararg args: String): ProcessResult {
        return localExecution.xNoLog("sh", "-c", "sudo docker exec $containerName " + buildCommand(*args))
    }

    fun installSudo(): ContainerUbuntuHostProcessor {
        a.containerSh(containerName, "apt-get update")
        a.containerSh(containerName, "apt-get -y install sudo")
        return this
    }

    fun addAndSwitchToUser(user: String = "testuser"): ContainerUbuntuHostProcessor {

        a.containerSh(containerName,"sudo useradd -m $user && echo '$user:$user' | chpasswd && adduser $user sudo")
        a.containerSh(containerName,"echo '$user ALL=(ALL:ALL) NOPASSWD: ALL' | sudo tee /etc/sudoers.d/$user")
        a.containerSh(containerName,"sudo su $user")
        a.containerSh(containerName,"cd /home/$user")
        a.containerSh(containerName,"mkdir $user && cd $user")
        return this
    }

    fun exitAndRm() {
        localExecution.x(SHELL, "-c", "sudo docker stop $containerName")
        localExecution.x(SHELL, "-c", "sudo docker rm $containerName")
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
