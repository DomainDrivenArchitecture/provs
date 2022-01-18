package org.domaindrivenarchitecture.provs.extensions.server_software.standalone_server.firewall.base

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


fun Prov.saveIpTablesToFile() = def {
    val dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("-yyyy-MM-dd--HH:mm:ss"))
    val file = "savedrules$dateTime.txt"
    sh("""
        sudo iptables-save > $file
        cat $file""")
}

fun Prov.restoreIpTablesFromFile(file: String? = null) = def {
    val fileName = file ?: cmd("ls -r a* | head -1\n").out
    fileName?.let { cmd("sudo iptables-restore < $file") }
        ?: ProvResult(false, err = "File to restore not found.")
}
