package org.domaindrivenarchitecture.provs.framework.ubuntu.scheduledjobs.infrastructure

import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDirs
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.fileContent
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.base.whoami
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class CronJobsKtTest {

    @ContainerTest
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


    @ContainerTest
//    @Disabled // only for manual execution and manual check for the created files
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
}