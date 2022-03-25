package org.domaindrivenarchitecture.provs.syspec.domain

import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.syspec.infrastructure.findSpecConfigFromFile
import org.domaindrivenarchitecture.provs.syspec.infrastructure.verifySpecConfig


fun Prov.verifySpec(config: ConfigFileName?) = task {
    val spec = findSpecConfigFromFile(config)

    if (spec == null) {
        ProvResult(false, "Could not read file: ${config?.fileName}")
    } else {
        verifySpecConfig(spec)
    }
}

