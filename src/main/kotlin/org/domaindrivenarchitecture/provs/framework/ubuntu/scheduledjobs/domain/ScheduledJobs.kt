package org.domaindrivenarchitecture.provs.framework.ubuntu.scheduledjobs.domain

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.scheduledjobs.infrastructure.createCronJob


/**
 * Adds a schedule for a monthly reboot of the (Linux) system.
 * ATTENTION: Use with care!! System will be shut down, restart might not succeed in all cases.
 */
fun Prov.scheduleMonthlyReboot() = task {
    // reboot each first Tuesday a month - see https://blog.healthchecks.io/2022/09/schedule-cron-job-the-funky-way/
    val schedule = "0 2 */100,1-7 * TUE"
    createCronJob("50_monthly_reboot", schedule, "/sbin/shutdown", "-r now", "root")
}
