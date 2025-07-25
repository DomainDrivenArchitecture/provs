package org.domaindrivenarchitecture.provs.framework.ubuntu.firewall.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Suppress("unused")
fun Prov.saveIpTablesToFile() = task {
    val dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("-yyyy-MM-dd--HH:mm:ss"))
    val file = "savedrules$dateTime.txt"
    sh("""
        sudo iptables-save > $file
        cat $file""")
}

@Suppress("unused")
fun Prov.restoreIpTablesFromFile(file: String? = null) = task {
    val fileName = file ?: cmd("ls -r a* | head -1\n").out
    fileName?.let { cmd("sudo iptables-restore < $file") }
        ?: ProvResult(false, err = "File to restore not found.")
}
