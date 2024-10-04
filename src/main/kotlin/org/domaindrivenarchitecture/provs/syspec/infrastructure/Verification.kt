package org.domaindrivenarchitecture.provs.syspec.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkDir
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.isPackageInstalled
import org.domaindrivenarchitecture.provs.syspec.domain.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Verifies all sub-specs of a SyspecConfig
 */
fun Prov.verifySpecConfig(conf: SyspecConfig) = task {
    conf.command?.let { task("CommandSpecs") { for (spec in conf.command) verify(spec) } }
    conf.file?.let { task("FileSpecs") { for (spec in conf.file) verify(spec) } }
    conf.folder?.let { task("FolderSpecs") { for (spec in conf.folder) verify(spec) } }
    conf.host?.let { task("HostSpecs") { for (spec in conf.host) verify(spec) } }
    conf.`package`?.let { task("PackageSpecs") { for (spec in conf.`package`) verify(spec) } }
    conf.netcat?.let { task("NetcatSpecs") { for (spec in conf.netcat) verify(spec) } }
    conf.socket?.let { task("SocketSpecs") { for (spec in conf.socket) verify(spec) } }
    conf.certificate?.let { task("CertificateFileSpecs") { for (spec in conf.certificate) verify(spec) } }
}

// -------------------------------  verification functions for individual specs  --------------------------------
fun Prov.verify(cmd: CommandSpec) {
    val res = cmdNoEval(cmd.command)
    if (!res.success) {
        verify(false, "Command [${cmd.command}] is not executable due to error: ${res.err}")
    } else {
        if (cmd.out == null) {
            verify(true, "Command is executable [${cmd.command}]")
        } else {
            val expected = cmd.out
            val actual = res.out?.trimEnd('\n')
            val contains = (actual?.contains(expected) ?: false)
            verify(
                contains,
                "Output of command [${cmd.command}] does ${contains.falseToNot()}contain [$expected] ${if (!contains) " - Actual output: [$actual]" else ""}"
            )
        }
    }
}

fun Prov.verify(file: FileSpec) {
    val actualExists = checkFile(file.name)
    verify(actualExists == file.exists, "File [${file.name}] does ${actualExists.falseToNot()}exist.")
}

fun Prov.verify(folder: FolderSpec) {
    val actualExists = checkDir(folder.path)
    verify(actualExists == folder.exists, "Folder [${folder.path}] does ${actualExists.falseToNot()}exist.")
}

fun Prov.verify(hostspec: HostSpec) {
    // see https://serverfault.com/questions/661978/displaying-a-remote-ssl-certificate-details-using-cli-tools
    val res =
        cmdNoEval("echo | openssl s_client -showcerts -servername ${hostspec.url} -connect ${hostspec.url}:443 2>/dev/null | openssl x509 -inform pem -noout -enddate")

    if (!res.success) {
        verify(false, "Could not retrieve certificate from [${hostspec.url}] due to error: ${res.err}")
    } else {
        if (hostspec.expirationDays == null) {
            verify(true, "Found a certificate on [${hostspec.url}]")
        } else {
            verifyCertExpiration(res.out, hostspec.url, hostspec.expirationDays)
        }
    }
}

fun Prov.verify(pkg: PackageSpec) {
    val res = isPackageInstalled(pkg.name)
    verify(res == pkg.installed, "Package [${pkg.name}] is ${res.falseToNot()}installed.")
}

fun Prov.verify(ncConf: NetcatSpec) {
    val timeout = 10 // sec
    val res = cmdNoEval("nc ${ncConf.host} ${ncConf.port} -z -w $timeout")
    verify(
        res.success == ncConf.reachable,
        "Host [${ncConf.host}] is ${res.success.falseToNot()}reachable at port [${ncConf.port}]."
    )
}

fun Prov.verify(socketConf: SocketSpec): ProvResult {
    val res = cmdNoEval("ss -tulpen", sudo = true)
    val lines: List<String> = res.out?.trim()?.split("\n") ?: emptyList()
    return if (lines.isEmpty()) {
        verify(false, "Could not get socketStats due to ${res.err}")
    } else {
        verifySocketSpec(socketConf, lines)
    }
}


fun Prov.verify(cert: CertificateFileSpec) {
    val res = cmdNoEval("openssl x509 -in ${cert.name} -noout -enddate")
    if (!res.success) {
        verify(false, "Could not retrieve certificate from [${cert.name}] due to error: ${res.err}")
    } else {
        verifyCertExpiration(res.out, cert.name, cert.expirationDays)
    }
}


// --------------------------  helper functions  ---------------------------------

fun Prov.verifySocketSpec(socketConf: SocketSpec, outputLines: List<String>): ProvResult {
    val headLine = outputLines[0]
    val processRange = "Process +".toRegex().find(headLine)?.range
    val ipRange = " +Local Address".toRegex().find(headLine)?.range
    val portRange = "Port +".toRegex().find(headLine)?.range
    val protocolRange = "Netid +".toRegex().find(headLine)?.range

    if (processRange == null || ipRange == null || portRange == null || protocolRange == null) {
        return verify(false, "Could not parse a headline from: $headLine")
    } else {
        val factLines: List<String> = outputLines.drop(1).filter { it.length == headLine.length }

        var matchingLine: String? = null
        for (line in factLines) {
            val process = "\"(.+)\"".toRegex().find(line.substring(processRange))?.groups?.get(1)?.value
            if (socketConf.processName == process &&
                socketConf.port.toString() == line.substring(portRange).trim() &&
                (socketConf.ip == null || socketConf.ip == line.substring(ipRange)) &&
                (socketConf.protocol == null || socketConf.protocol == line.substring(protocolRange))
            ) {
                matchingLine = line
                break
            }
        }
        val found = matchingLine != null
        return verify(found == socketConf.running, "Did ${found.falseToNot()}find [$socketConf]")
    }
}

private fun Boolean.falseToNot(suffix: String = " ") = if (this) "" else "not$suffix"

private fun Prov.verify(success: Boolean, message: String): ProvResult {
    return verify<Any>(success, message, null, null)
}

private fun <T> Prov.verify(success: Boolean, message: String, expected: T? = null, actual: T? = null): ProvResult {
    val expectedText = expected?.let { " | Expected value [$expected]" } ?: ""
    val actualText = expected?.let { " | Actual value [$actual]" } ?: ""
    val msg = ": $message $expectedText$actualText"

    return taskWithResult("Verification") {
        ProvResult(
            success,
            cmd = if (success) msg else null,
            err = if (!success) msg else null,
        )
    }
}

private data class DiffResult(val diff: Long? = null, val err: String? = null) {
    init {
        require(diff != null || err != null)
    }
}

private fun diffSslDateToToday(enddate: String?): DiffResult {
    val format = SimpleDateFormat("MMM d HH:mm:ss yyyy zzz", Locale.ENGLISH)
    return try {
        val expirationDate = format.parse(enddate)
        val diffInMillisec: Long = expirationDate.time - Date().time
        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillisec)
        DiffResult(diff = diffInDays)
    } catch (e: ParseException) {
        DiffResult(err = "Could not parse date '$enddate' with pattern '$format' - Parse error: ${e.message}")
    }
}

private fun Prov.verifyCertExpiration(enddate: String?, certName: String, expirationDays: Long) {
    val enddateCleaned = enddate?.replace("notAfter=", "")?.trimEnd('\n')
    val (diffInDays, err) = diffSslDateToToday(enddateCleaned)
    if (diffInDays == null) {
        verify(false, err ?: ("Could not parse date: $enddateCleaned"))
    } else {
        verify(
            diffInDays > expirationDays,
            "Certificate of [$certName] expires on [${enddateCleaned}] in $diffInDays days (expected > $expirationDays days)",
        )
    }
}
