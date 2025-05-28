package org.domaindrivenarchitecture.provs.server.domain.k3s

import java.io.File

class ApplicationFileName(private val fqFileName: String) {
    fun absolutePath() : String {
        return File(fqFileName).absolutePath
    }
    fun name() : String {
        return File(fqFileName).name
    }
    override fun toString(): String {
        return fqFileName
    }
}