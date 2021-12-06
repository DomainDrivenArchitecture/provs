package org.domaindrivenarchitecture.provs.workplace.application

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.workplace.domain.provisionWorkplace
import org.domaindrivenarchitecture.provs.workplace.domain.WorkplaceConfig

/**
 * Use case for provisioning a workplace
 */
fun Prov.provision(conf: WorkplaceConfig) = def {
    with (conf) {
        provisionWorkplace(type, ssh?.keyPair(), gpg?.keyPair(), gitUserName, gitEmail)
    }
}
