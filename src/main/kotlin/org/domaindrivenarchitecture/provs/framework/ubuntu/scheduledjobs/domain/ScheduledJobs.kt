package org.domaindrivenarchitecture.provs.framework.ubuntu.scheduledjobs.domain

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.scheduledjobs.infrastructure.createCronJob
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkFile


/**
 * Adds a cronJob for a monthly reboot of the (Linux) system.
 * ATTENTION: Use with care!!
 */
fun Prov.scheduleMonthlyReboot() = task {
    // use controlled "shutdown" instead of direct "reboot"
    val shutdown = "/sbin/shutdown"

    // ensure shutdown command exists
    if (checkFile(shutdown, sudo = true)) {
        // reboot each first Tuesday in a month at 3:00
        createCronJob("50_monthly_reboot", "0 2 1-7 * 2", "$shutdown -r now", "root")
    } else {
        addResultToEval(ProvResult(false, err = "$shutdown not found."))
    }
}
