package org.domaindrivenarchitecture.provs.server.domain.k3s

import java.io.File

class ApplicationFileName(val fileName: String) {
    fun fullyQualifiedName() : String {
        return File(fileName).absoluteFile.absolutePath
    }
}