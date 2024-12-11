package org.domaindrivenarchitecture.provs.server.domain.hetzner_csi

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.server.infrastructure.provisionHetznerCSIForK8s

fun Prov.provisionHetznerCSI(configResolved: HetznerCSIConfigResolved) = task {
    provisionHetznerCSIForK8s(configResolved.hcloudApiToken, configResolved.encryptionPassphrase)
}