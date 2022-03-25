package org.domaindrivenarchitecture.provs.syspec.domain

import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.syspec.infrastructure.findSpecConfigFromFile
import org.domaindrivenarchitecture.provs.syspec.infrastructure.findSpecConfigFromResource
import org.domaindrivenarchitecture.provs.syspec.infrastructure.verifySpecConfig


fun Prov.verifySpec(configFile: ConfigFileName? = null) = task {
    val spec = findSpecConfigFromFile(configFile)

    if (spec == null) {
        ProvResult(false, "Could not read file: ${configFile?.fileName}")
    } else {
        verifySpecConfig(spec)
    }
}


@Suppress("unused")   // Api
fun Prov.verifySpecFromResource(resourceName: String) = task {
    val spec = findSpecConfigFromResource(resourceName)

    if (spec == null) {
        ProvResult(false, "Could not read resource: ${resourceName}")
    } else {
        verifySpecConfig(spec)
    }
}

