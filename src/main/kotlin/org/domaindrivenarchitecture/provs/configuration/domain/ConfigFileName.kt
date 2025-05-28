package org.domaindrivenarchitecture.provs.configuration.domain

import java.io.File

class ConfigFileName(fileName: String)
{
    val fileName = fileName.trim()
    fun absolutePath() : String {
        return File(fileName).absolutePath
    }
}
