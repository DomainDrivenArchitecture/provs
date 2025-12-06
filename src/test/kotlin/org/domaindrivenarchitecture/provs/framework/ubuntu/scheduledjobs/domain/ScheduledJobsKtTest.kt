package org.domaindrivenarchitecture.provs.framework.ubuntu.scheduledjobs.domain

import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.checkFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.createFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.fileContent
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.*

class ScheduledJobsKtTest {

    @ContainerTest
    fun tests_scheduleMonthlyReboot() {
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
        assertEquals("03 5 */100,1-7 * TUE root /sbin/shutdown -r now\n", actualFileContent)
    }
}