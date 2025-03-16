package org.domaindrivenarchitecture.provs.framework.ubuntu.scheduledjobs.domain

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.scheduledjobs.infrastructure.createCronJob


/**
 * Adds a schedule for a monthly reboot of the (Linux) system.
 * ATTENTION: Use with care!! System will be shut down, restart might not succeed in all cases.
 */
fun Prov.scheduleMonthlyReboot() = task {
    // reboot each first Tuesday a month - see also https://blog.healthchecks.io/2022/09/schedule-cron-job-the-funky-way/
    // Day of month field "*/100,1-7" means “every 100 days starting from date 1, and also on dates 1-7”.
    // Since there are no months with 100 days, this means “on dates 1 to 7” that are also (in this case) Tuesdays, which is exactly each first Tuesday in a month..
    val schedule = "0 2 */100,1-7 * TUE"
    createCronJob("50_monthly_reboot", schedule, "/sbin/shutdown", "-r now", "root")
}
