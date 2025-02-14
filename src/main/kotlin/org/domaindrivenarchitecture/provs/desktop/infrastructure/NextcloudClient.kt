package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov


fun Prov.installNextcloudClient() = task {
    cmd("apt-get -q=2 update && apt-get -q=2 upgrade", sudo = true)
    //large stdout breaks install, redirect stdout to /dev/null
    cmd("apt-get install -q=2 nextcloud-desktop 1>/dev/null", sudo = true)
}
