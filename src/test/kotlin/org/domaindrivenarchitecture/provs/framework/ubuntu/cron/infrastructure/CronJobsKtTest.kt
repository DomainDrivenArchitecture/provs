package org.domaindrivenarchitecture.provs.framework.ubuntu.cron.infrastructure

import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDirs
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.fileContent
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.base.whoami
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class CronJobsKtTest {

    @Test
    fun createCronJob_creates_cron_file() {
        // given
        val prov = defaultTestContainer()
        val cronFilename = "50_test_cron"

        // when
        val result = prov.createCronJob(cronFilename, "0 * * * *", "echo hello > /dev/null 2>&1")

        // then
        assertTrue(result.success)
        val fqFilename = "/etc/cron.d/$cronFilename"
        assertTrue(prov.checkFile(fqFilename), "")
        val actualFileContent = prov.fileContent(fqFilename, sudo = true)
        val expectedUser = prov.whoami()
        assertEquals("0 * * * * $expectedUser echo hello > /dev/null 2>&1\n", actualFileContent)
    }


    @Test
    @Disabled // only for manual execution and manual check for the created files
    // Test if cron-job is actually running, but needs manual checks
    fun createCronJob_which_creates_files_with_timestamp() {
        // given
        val prov = defaultTestContainer()
        val cronFilename = "90_time_cron"

        // for cron in docker see e.g. https://serverfault.com/questions/924779/docker-cron-not-working
        prov.task {
            aptInstall("cron")
            cmd("sudo touch /var/log/cron.log")    // Create the log file
            optional {                          // may already be running
                cmd("sudo cron")                   // Run cron
            }
            cmd("pgrep cron")                      // Ensure cron is running
        }

        prov.createDirs("tmp")
        val user = prov.whoami()

        // when
        val result = prov.createCronJob(
            cronFilename,
            "*/1 * * * *",
            "echo \"xxx\" > /home/$user/tmp/\$(/usr/bin/date +\\%Y_\\%m_\\%d-\\%H_\\%M)"
        )

        // then
        assertTrue(result.success)
        val fqFilename = "/etc/cron.d/$cronFilename"
        assertTrue(prov.checkFile(fqFilename), "File does not exist: $fqFilename")

        // after a minute check manually if files exist, e.g. with: sudo docker exec provs_test /bin/bash -c "ls -l tmp"
        // each minute a new file should be created with the timestamp
    }


    @Test
    fun scheduleMonthlyReboot() {
        // given
        val prov = defaultTestContainer()
        // create dummy shutdown in test container if missing (containers do usually not have shutdown installed)
        prov.createFile(
            "/sbin/shutdown",
            "dummy file for test of scheduleMonthlyReboot",
            sudo = true,
            overwriteIfExisting = false
        )

        // when
        val result = prov.scheduleMonthlyReboot()

        // then
        assertTrue(result.success)
        val fqFilename = "/etc/cron.d/50_monthly_reboot"
        assertTrue(prov.checkFile(fqFilename), "")
        val actualFileContent = prov.fileContent(fqFilename, sudo = true)
        assertEquals("0 3 1-7 * 2 root shutdown -r now\n", actualFileContent)
    }
}