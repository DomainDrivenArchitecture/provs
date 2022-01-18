package org.domaindrivenarchitecture.provs.framework.core.processors

import org.domaindrivenarchitecture.provs.framework.core.escapeNewline
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.charset.Charset


private fun getOsName(): String {
    return System.getProperty("os.name")
}

open class LocalProcessor : Processor {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val log = LoggerFactory.getLogger(javaClass.enclosingClass)

        var charset: Charset = Charset.defaultCharset()
        init {
            log.info("os.name: " + getOsName())
            log.info("user.home: " + System.getProperty("user.home"))
        }
    }

    private fun workingDir() : String
    {
        return System.getProperty("user.home") ?: File.separator
    }

    override fun x(vararg args: String): ProcessResult {
        return execute(true, *args)
    }


    override fun xNoLog(vararg args: String): ProcessResult {
        return execute(false, *args)
    }

    private fun execute(logging: Boolean, vararg args: String): ProcessResult {
        try {
            var prefix = "******************** Prov: "
            if (logging) {
                for (arg in args) {
                    prefix += " \"${arg.escapeNewline()}\""
                }
            } else {
                prefix += "\"xxxxxxxx\""
            }
            log.info(prefix)

            val proc = ProcessBuilder(args.toList())
                    .directory(File(workingDir()))
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE)
                    .start()

            val c = proc.waitFor()

            val r = ProcessResult(
                c,
                proc.inputStream.bufferedReader(charset).readText(),
                proc.errorStream.bufferedReader(charset).readText(),
                args = args
            )
            if (logging) {
                log.info(r.toString())
            }
            return r

        } catch (e: IOException) {
            e.printStackTrace()
            return ProcessResult(-1, ex = e)
        }
    }
}
