package org.domaindrivenarchitecture.provs.server.domain.k3s

import org.domaindrivenarchitecture.provs.framework.core.getLocalFileContent
import java.io.File

data class ApplicationFile(val id: ApplicationFileName, val fileContent: String) {

    fun validate() : List<String> {
        val output = ArrayList<String>()
        val specRegex = "Spec.failed".toRegex()
        val javaRegex = "Exception.in.thread".toRegex()

        if(fileContent.isEmpty()) {
            output.add("fileContent is empty.")
        }
        if (fileContent.contains(specRegex)) {
            output.add(specRegex.find(fileContent)!!.value)
        }
        if (fileContent.contains(javaRegex)) {
            output.add(javaRegex.find(fileContent)!!.value)
        }

        return output
    }
    fun isValid() : Boolean {
        return validate().isEmpty()
    }
}
