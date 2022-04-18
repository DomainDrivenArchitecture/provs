package org.domaindrivenarchitecture.provs.syspec.domain

import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.syspec.infrastructure.findSpecConfigFromFile
import org.domaindrivenarchitecture.provs.syspec.infrastructure.findSpecConfigFromResource
import org.domaindrivenarchitecture.provs.syspec.infrastructure.verifySpecConfig


fun Prov.verifySpec(configFile: ConfigFileName? = null) = taskWithResult {
    val result = findSpecConfigFromFile(configFile)
    val spec = result.getOrElse { return@taskWithResult ProvResult(false, "Could not read file: ${configFile?.fileName} due to: ${result.exceptionOrNull()?.message}") }
    verifySpecConfig(spec)
}


@Suppress("unused")   // Api
fun Prov.verifySpecFromResource(resourceName: String) = taskWithResult {
    val result = findSpecConfigFromResource(resourceName)
    val spec = result.getOrElse { return@taskWithResult ProvResult(false, "Could not read resource: $resourceName due to: ${result.exceptionOrNull()?.message}") }
    verifySpecConfig(spec)
}

