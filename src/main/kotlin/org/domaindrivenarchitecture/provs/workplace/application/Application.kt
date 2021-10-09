package org.domaindrivenarchitecture.provs.workplace.application

import org.domaindrivenarchitecture.provs.core.Password
import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.ProvResult
import org.domaindrivenarchitecture.provs.workplace.domain.WorkplaceConfig
import org.domaindrivenarchitecture.provs.workplace.domain.WorkplaceType
import org.domaindrivenarchitecture.provs.workplace.infrastructure.installDevOps

/**
 * Use case for provisioning repos
 */
fun Prov.provision(conf: WorkplaceConfig) = def {
    if (conf.type == WorkplaceType.IDE) {
        installDevOps()
    }
    ProvResult(true)
}
