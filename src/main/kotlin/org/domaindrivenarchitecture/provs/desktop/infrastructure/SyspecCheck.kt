package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.syspec.domain.verifySpecFromResource

fun Prov.verifyIdeSetup(): ProvResult = task {
    verifySpecFromResource("syspec-ide-config.yaml")
}

fun Prov.verifyOfficeSetup(): ProvResult = task {
    verifySpecFromResource("syspec-office-config.yaml")
}