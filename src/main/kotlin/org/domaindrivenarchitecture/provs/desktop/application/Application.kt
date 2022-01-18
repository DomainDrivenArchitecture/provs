package org.domaindrivenarchitecture.provs.desktop.application

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.desktop.domain.provisionWorkplace
import org.domaindrivenarchitecture.provs.desktop.domain.WorkplaceConfig

/**
 * Use case for provisioning a workplace
 */
fun provision(prov: Prov, conf: WorkplaceConfig) {
    with (conf) {
        prov.provisionWorkplace(type, ssh?.keyPair(), gpg?.keyPair(), gitUserName, gitEmail)
    }
}
