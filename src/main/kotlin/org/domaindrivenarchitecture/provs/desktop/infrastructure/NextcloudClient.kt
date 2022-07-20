package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstallFromPpa


fun Prov.installNextcloudClient() = task {
    aptInstallFromPpa("nextcloud-devs", "client", "nextcloud-client")
}
