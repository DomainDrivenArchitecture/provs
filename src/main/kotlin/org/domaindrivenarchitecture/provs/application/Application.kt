package org.domaindrivenarchitecture.provs.application

import org.domaindrivenarchitecture.provs.core.Password
import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.ProvResult
import org.domaindrivenarchitecture.provs.domain.WorkplaceConfig
import org.domaindrivenarchitecture.provs.domain.WorkplaceType
import org.domaindrivenarchitecture.provs.infrastructure.installDevOps

/**
 * Use case for provisioning repos
 */
fun Prov.provision(conf: WorkplaceConfig, let: Password?) = def {
    if (conf.type == WorkplaceType.IDE) {
        installDevOps()
    }
    ProvResult(true)
}
