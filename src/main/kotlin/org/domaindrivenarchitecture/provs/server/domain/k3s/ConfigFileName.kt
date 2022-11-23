package org.domaindrivenarchitecture.provs.server.domain.k3s

import java.io.File

data class ConfigFileName(val fileName: String) {
    fun fullqualified() : String {
        return File(fileName).absoluteFile.absolutePath
    }
}