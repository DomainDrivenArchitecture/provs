package org.domaindrivenarchitecture.provs.workplace.application

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.Secret
import org.domaindrivenarchitecture.provs.extensions.workplace.provisionWorkplace
import org.domaindrivenarchitecture.provs.workplace.domain.WorkplaceConfig

/**
 * Use case for provisioning a workplace
 */
fun Prov.provision(conf: WorkplaceConfig, password: Secret?) = def {
    with (conf) {
        provisionWorkplace(type, ssh?.keyPair(), gpg?.keyPair(), gitUserName, gitEmail, password)
    }
}
