package org.domaindrivenarchitecture.provs.server.domain.k3s

import java.io.File

class ApplicationFileName(val fqFileName: String) {
    fun absoluteFileName() : String {
        return File(fqFileName).absoluteFile.absolutePath
    }
    fun name() : String {
        return File(fqFileName).name
    }
    override fun toString(): String {
        return fqFileName
    }
}