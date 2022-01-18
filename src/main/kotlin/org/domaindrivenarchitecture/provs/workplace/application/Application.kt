package org.domaindrivenarchitecture.provs.workplace.application

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.workplace.domain.provisionWorkplace
import org.domaindrivenarchitecture.provs.workplace.domain.WorkplaceConfig

/**
 * Use case for provisioning a workplace
 */
fun provision(prov: Prov, conf: WorkplaceConfig) {
    with (conf) {
        prov.provisionWorkplace(type, ssh?.keyPair(), gpg?.keyPair(), gitUserName, gitEmail)
    }
}
