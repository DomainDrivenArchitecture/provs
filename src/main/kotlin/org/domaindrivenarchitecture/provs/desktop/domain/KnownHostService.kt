package org.domaindrivenarchitecture.provs.desktop.domain

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base.addKnownHost


fun Prov.addKnownHosts(knownHosts: List<KnownHost> = KnownHost.values()) = task {
    for (knownHost in knownHosts) {
        with(knownHost) {
            addKnownHost(hostName, hostKeys, verifyKeys = true)
        }
    }
}