package org.domaindrivenarchitecture.provs.framework.ubuntu.scheduledjobs.domain

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.scheduledjobs.infrastructure.createCronJob


/**
 * Adds a schedule for a monthly reboot of the (Linux) system.
 * ATTENTION: Use with care!! System will be shut down, restart might not succeed in all cases.
 */
fun Prov.scheduleMonthlyReboot() = task {
    // reboot each first Tuesday in a month at 3:00
    createCronJob("50_monthly_reboot", "0 2 1-7 * 2", "/sbin/shutdown", "-r now", "root")
}
