package org.domaindrivenarchitecture.provs.server.domain.k3s

data class ApplicationFile(val id: ApplicationFileName, val fileContent: String) {

    fun validate() : List<String> {
        val output = ArrayList<String>()
        val specRegex = "Spec.failed".toRegex()
        val javaRegex = "Exception.in.thread".toRegex()

        if(fileContent.isEmpty()) {
            output.add("fileContent is empty.")
        }
        val specMatch = specRegex.find(fileContent)
        if (specMatch != null) {
            output.add(specMatch.value)
        }
        val javaMatch = javaRegex.find(fileContent)
        if (javaMatch != null) {
            output.add(javaMatch.value)
        }
        return output
    }
    fun isValid() : Boolean {
        return validate().isEmpty()
    }
}
