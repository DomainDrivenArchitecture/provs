package org.domaindrivenarchitecture.provs.framework.ubuntu.scheduledjobs.domain

import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.fileContent
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ScheduledJobsKtTest {

    @Test
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
        assertEquals("0 2 1-7 * 2 root /sbin/shutdown -r now\n", actualFileContent)
    }
}