package org.domaindrivenarchitecture.provs.framework.ubuntu.scheduledjobs.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDirs
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.base.whoami


/**
 * Creates a cron job.
 * @param cronFilename e.g. "90_my_cron"; file is created in folder /etc/cron.d/
 * @param schedule in the usual cron-format, examples: "0 * * * *" for each hour, "0 3 1-7 * 1" for the first Monday each month at 3:00, etc
 * @param command the executed command
 * @param user the user with whom the command will be executed, if null the current user is used
 */
fun Prov.createCronJob(cronFilename: String, schedule: String, command: String, user: String? = null) = task {
    val cronUser = user ?: whoami()
    val cronLine = "$schedule $cronUser $command\n"

    createDirs("/etc/cron.d/", sudo = true)
    createFile("/etc/cron.d/$cronFilename", cronLine, "644", sudo = true, overwriteIfExisting = true)
}
