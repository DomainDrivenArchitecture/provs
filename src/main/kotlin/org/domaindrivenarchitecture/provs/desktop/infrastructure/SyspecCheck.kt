package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.syspec.domain.verifySpecFromResource

private const val resourcePath = "org/domaindrivenarchitecture/provs/syspec/"

fun Prov.verifyIdeSetup(): ProvResult = task {
    verifySpecFromResource("${resourcePath}syspec-ide-config.yaml")
}

fun Prov.verifyOfficeSetup(): ProvResult = task {
    verifySpecFromResource("${resourcePath}syspec-office-config.yaml")
}