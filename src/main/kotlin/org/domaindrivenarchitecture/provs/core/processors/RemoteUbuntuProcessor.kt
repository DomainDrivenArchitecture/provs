package org.domaindrivenarchitecture.provs.core.processors

import org.domaindrivenarchitecture.provs.core.Secret
import org.domaindrivenarchitecture.provs.core.escapeAndEncloseByDoubleQuoteForShell
import org.domaindrivenarchitecture.provs.core.escapeNewline
import org.domaindrivenarchitecture.provs.core.platforms.SHELL
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.connection.channel.direct.Session
import net.schmizz.sshj.connection.channel.direct.Session.Command
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.net.InetAddress
import java.util.concurrent.TimeUnit


class RemoteProcessor(ip: InetAddress, user: String, password: Secret? = null) : Processor {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val log = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    private val ssh = SSHClient()

    init {
        try {
            log.info("Connecting to $ip with user: $user with " + if (password != null) "password" else "ssh-key")

            ssh.loadKnownHosts()

            // todo: replace PromiscuousVerifier by more secure solution
            ssh.addHostKeyVerifier(PromiscuousVerifier())
            ssh.connect(ip)

            if (password != null) {
                ssh.authPassword(user, password.plain())
            } else {
                val base = System.getProperty("user.home") + File.separator + ".ssh" + File.separator
                ssh.authPublickey(user, base + "id_rsa", base + "id_dsa", base + "id_ed25519", base + "id_ecdsa")
            }
        } catch (e: Exception) {
            try {
                ssh.disconnect()
            } finally {
                log.error("Got exception when initializing ssh (Username, password or ssh-key might be wrong): " + e.message)
                throw RuntimeException("Error when initializing ssh (Username, password or ssh-key might be wrong) ", e)
            }
        }
    }

    override fun x(vararg args: String): ProcessResult {
        return execute(true, *args)
    }

    override fun xNoLog(vararg args: String): ProcessResult {
        return execute(false, *args)
    }

    private fun execute(logging: Boolean, vararg args: String): ProcessResult {
        var prefix = "******************** Prov: "
        if (logging) {
            for (arg in args) {
                prefix += " \"${arg.escapeNewline()}\""
            }
        } else {
            prefix += "\"xxxxxxxx\""
        }
        log.info(prefix)

        val cmdString: String =
            if (args.size == 1)
                args[0].escapeAndEncloseByDoubleQuoteForShell()
            else
                if (args.size == 3 && SHELL == args[0] && "-c" == args[1])
                    SHELL + " -c " + args[2].escapeAndEncloseByDoubleQuoteForShell()
                else
                    args.joinToString(separator = " ")

        var session: Session? = null

        try {
            session = ssh.startSession()

            val cmd: Command = session!!.exec(cmdString)
            val out = BufferedReader(InputStreamReader(cmd.inputStream)).use { it.readText() }
            val err = BufferedReader(InputStreamReader(cmd.errorStream)).use { it.readText() }
            cmd.join(100, TimeUnit.SECONDS)

            val cmdRes = ProcessResult(cmd.exitStatus, out, err, args = args)
            if (logging) {
                log.info(cmdRes.toString())
            }
            session.close()

            return cmdRes

        } catch (e: Exception) {
            try {
                session?.close()
            } finally {
                // nothing to do
            }
            return ProcessResult(
                -1,
                err = "Error when opening or executing remote ssh session (Pls check host, user, password resp. ssh key) - ",
                ex = e
            )
        }
    }

    override fun close() {
        try {
            log.info("Disconnecting ssh.")
            ssh.disconnect()
        } catch (e: IOException) {
            // No prov required
        }
    }

    protected fun finalize() {
        close()
    }
}
