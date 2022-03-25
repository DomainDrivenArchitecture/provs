package org.domaindrivenarchitecture.provs.syspec.domain

import kotlinx.serialization.Serializable

@Serializable
data class SpecConfig(
    val command: List<CommandSpec>? = null,
    val file: List<FileSpec>? = null,
    val host: List<HostSpec>? = null,
    val `package`: List<PackageSpec>? = null,
    val netcat: List<NetcatSpec>? = null,
    val socket: List<SocketSpec>? = null,
    val certificate: List<CertificateFileSpec>? = null,
)


/**
 * Checks that a command executes successfully and
 * (if provided) the specified output is contained in the actual output
 */
@Serializable
data class CommandSpec(val command: String, val out: String? = null)

@Serializable
data class FileSpec(val name: String, val exists: Boolean = true)

@Serializable
data class HostSpec(val url: String, val expirationDays: Long? = null)

@Serializable
data class PackageSpec(val name: String, val installed: Boolean = true)

@Serializable
data class NetcatSpec(val host: String, val port: Int = 80, val reachable: Boolean = true)

@Serializable
data class SocketSpec(
    val processName: String,
    val port: Int,
    val running: Boolean = true,
    val ip: String? = null,
    val protocol: String? = null
)

@Serializable
data class CertificateFileSpec(val name: String, val expirationDays: Long)
